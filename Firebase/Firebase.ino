#include<ESP8266WiFi.h>
#include<FirebaseESP8266.h>
#include <ArduinoJson.h>
#include <OneWire.h>
#include <DallasTemperature.h>
#define FIREBASE_HOST "doan-2d278-default-rtdb.firebaseio.com"
#define FIREBASE_AUTH "mS8wG2HF1hyI7k9u83gZ8TYvGESVNmNhySnhxRu0"
#define WIFI_SSID "B COFFEE"
#define WIFI_PASSWORD "208hoangdieu2"
#define trig   2 //D4
#define echo  14  //D5
#define pumpPin  0 //D3
const int oneWireBus = 12; 
boolean pumpState = 0;
String  mode_text;
FirebaseData firebaseData;
String path = "/";
FirebaseJson json;
long duration;
float distance;
float temperatureC;
OneWire oneWire(oneWireBus);
DallasTemperature sensors(&oneWire);
void setup()
{
  pinMode(trig, OUTPUT);
  pinMode(echo, INPUT);
  pinMode(pumpPin, OUTPUT);
  digitalWrite(pumpPin,0);
  Serial.begin(9600);
  sensors.begin();
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  while(WiFi.status() != WL_CONNECTED)
  {
    Serial.print(".");
    delay(500);
    }
  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);
  Firebase.reconnectWiFi(true);
  if (!Firebase.beginStream(firebaseData,path))
  {
    Serial.println("REASON: " + firebaseData.errorReason());
    Serial.println();
    }
  Serial.println("Connect with IP: ");
  Serial.println(WiFi.localIP());
  Serial.println();

 
  }
  void loop()
  {
       digitalWrite(trig,LOW);
     delayMicroseconds(2);
    digitalWrite(trig, HIGH);
    delayMicroseconds(10);
    digitalWrite(trig, LOW);
    duration = pulseIn(echo,HIGH);
    distance = duration*0.017;
    Serial.print("KHOANG CACH:");
    Serial.print(distance);
    Serial.print("CM");
    Serial.println("");
    sensors.requestTemperatures(); 
    temperatureC = sensors.getTempCByIndex(0);
    Serial.print(temperatureC);
    Serial.print("ºC");
    Serial.println("");
    Firebase.setFloat(firebaseData, path + "/mayBom/status",distance);
    Firebase.setFloat(firebaseData, path + "/mayBom/temp",temperatureC);
    if(Firebase.getBool(firebaseData, path + "/mayBom/mayBom")) 
      {
        pumpState = firebaseData.boolData();
        Serial.print("pumpstate:");
        Serial.print(pumpState);
        Serial.println("");
      }
      
    // Điều khiển máy bơm dựa trên trạng thái và giá trị của cảm biến siêu âm
    if(Firebase.getString(firebaseData, path + "/mayBom/mode"))
      {
        mode_text = firebaseData.stringData();
        Serial.print("mode:");
        Serial.print(mode_text);
        Serial.println("");
      }


     //Chế độ tự động
     if(mode_text == "tuDong")
     {
      if ( distance < 50) 
      digitalWrite(pumpPin, HIGH);
      else 
      digitalWrite(pumpPin, LOW);
     }
     //Chế độ thủ công
     else if(mode_text == "thuCong")
     {
      if(pumpState == 1)
        digitalWrite(pumpPin, HIGH);
       else
        digitalWrite(pumpPin, LOW);
     }
    }
  
