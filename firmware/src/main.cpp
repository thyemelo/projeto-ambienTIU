#include <Arduino.h>
//Importação das bibliotecas
#include <WiFi.h>
#include <SPI.h>
#include <Wire.h>
#include <Adafruit_SSD1306.h>
#include <DHT.h>
#include <HTTPClient.h>
#include <esp_sleep.h>

//Definição dos parâmetros do display
#define LARGURA_DISPLAY 128
#define ALTURA_DISPLAY 64
#define RESET_OLED -1

//Definição do pino e do tipo do sensor DHT
#define pinoDHT 18
#define tipoDHT DHT11

//Definição do pino do LDR, botão e led de conexão
#define LDR 35
#define bt_reset 33
#define led_conexao 34

//Definição dos parâmetros da rede wifi
const char* ssid = "IOLANDA - Oi FIBRA 2.4/5G";
const char* pass = "valente2";
const char* url_server = "";

//Criação do objeto do display
Adafruit_SSD1306 display(LARGURA_DISPLAY, ALTURA_DISPLAY, &Wire, RESET_OLED);

//Criação do objeto do sensor DHT
DHT dht(pinoDHT, tipoDHT);

//Valores dos sensores
int temperatura = 0;
int umidade = 0;
int iluminacao = 0;

//Declaração das funções
int ler_temperatura_dht(int temperatura);
int ler_umidade_dht(int umidade);
int ler_ldr(int iluminacao);
void saida_display(int temperatura, int umidade, int iluminacao);
void IRAM_ATTR reset_esp();
void enviar_dados_servidor();

void setup() {
  //Inicialização dos componentes
  Serial.begin(115200);

  //Inicialização do led
  pinMode(led_conexao, OUTPUT);

  //Configuração do tempo de baixo consumo de energia
  esp_sleep_enable_timer_wakeup(20 * 1000000);

  //Inicialização do LDR e do botão
  pinMode(LDR, INPUT);
  pinMode(bt_reset, INPUT_PULLUP);

  //Inicialização do display
  display.begin(SSD1306_SWITCHCAPVCC, 0x3C);
  display.clearDisplay();

  //Inicialização do sensor dht
  dht.begin();

  //Inicialização do WiFi
  WiFi.begin(ssid, pass);

  //Criando a interrupção para o reset
  attachInterrupt(bt_reset, reset_esp, FALLING);
}


void loop() {
  ler_temperatura_dht(temperatura);
  ler_umidade_dht(umidade);
  ler_ldr(iluminacao);
  saida_display(ler_temperatura_dht(temperatura), ler_umidade_dht(umidade), ler_ldr(iluminacao));
  esp_light_sleep_start();
}


//Função de leitura da temperatura do sensor DHT11
int ler_temperatura_dht(int temperatura){
  temperatura = dht.readTemperature();
  //Verificação de leitura dos dados do sensor
  if(isnan(temperatura)){
    Serial.println("Falha na leitura. (DHT/Temp)");
    return 0;
  }

  Serial.print("Temperatura: "); 
  Serial.println(temperatura);
  return temperatura;
}

//Função da leitura de umidade do sensor DHT11
int ler_umidade_dht(int umidade){
  umidade = dht.readHumidity();
  if(isnan(umidade)){
    Serial.println("Falaha na leitura. (DHT/Umid)");
    return 0;
  }

  Serial.print("Umidade: ");
  Serial.println(umidade);
  return umidade;
}

//Função de leitura do LDR
int ler_ldr(int iluminacao){
  iluminacao = analogRead(LDR);

  if(isnan(iluminacao)){
    Serial.println("Falha na leitura (LDR)");
    return 0;
  }

  Serial.print("Iluminação: ");
  Serial.println(iluminacao);
  return iluminacao;
}

//Função de reset do controlador
void IRAM_ATTR reset_esp(){
  Serial.println("Resetando o ESP32...");
  ESP.restart();
}

//Função que envia os dados para o servidor backend
void enviar_dados_servidor(int temperatura, int umidade, int iluminacao){
  //Verificação do WiFi
  if(WiFi.status() != WL_CONNECTED){
    delay(200);
    digitalWrite(led_conexao, !led_conexao);
    Serial.println(".");
  }
  Serial.println("Conectado!");
  Serial.println(WiFi.localIP());

  //Criação dos objetos e definição de tempo de timeout
  WiFiClient client;
  HTTPClient http;
  http.setTimeout(15000);

  //Envio de dados
  String url = String(url_server);
  String dados_json = "{\"Temperatura\":" + String(temperatura) + ",\"Umidade\":" + String(umidade) + ",\"Iluminação\":" + String(iluminacao) + "  }";
  Serial.println("Json a ser enviado: " + dados_json);
  http.begin(client, url);
  http.addHeader("Content-Type", "application/json");
  int httpCode = http.POST(dados_json);

  //Verificação do envio
  if(httpCode > 0){
    Serial.println("[HTTP] POST - Envio bem sucedido! Código " + httpCode);
    String resposta = http .getString();
    Serial.println("Resposta do servidor: " + resposta);
  }else{
    Serial.print("[HTTP] POST - Falha no envio dos dados. Código ");
    Serial.println(http.errorToString(httpCode).c_str());
  }
  http.end();
}

//Função que mostra as informações no display em tempo real
void saida_display(int temperatura, int umidade, int iluminacao){
  display.setTextSize(2);
  display.setTextColor(WHITE);
  display.setCursor(0,0);
  display.print("Ambiente");

  //Mostra a temperatura
  display.setTextSize(1);
  display.setCursor(0,20);
  display.print("Temp: ");
  display.setCursor(30, 20);
  display.print(temperatura);

  //Mostra a Umidade
  display.setCursor(0, 35);
  display.print("Umid: ");
  display.setCursor(30,35);
  display.print(umidade);

  //Mostra a luminosidade
  display.setCursor(0, 50);
  display.print("Lumin: ");
  display.setCursor(40, 50);
  display.print(iluminacao);

  display.display();
  display.clearDisplay();
}