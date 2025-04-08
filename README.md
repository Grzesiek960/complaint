Complaint Management API
=========================

Wprowadzenie
------------
Projekt to przykładowa aplikacja REST API do zarządzania reklamacjami.
Aplikacja umożliwia:
- Dodawanie nowych reklamacji – przy próbie dodania zgłoszenia dla danego produktu i użytkownika, licznik zgłoszeń zostaje inkrementowany.
- Edycję treści istniejących reklamacji.
- Pobieranie pojedynczej lub wszystkich reklamacji.
- Automatyczne ustalanie kraju na podstawie adresu IP klienta.

Projekt został przygotowany, wykorzystując:
- Java 17
- Spring Boot 3.1.2
- Maven
- PostgreSQL jako bazę danych
- Liquibase do migracji bazy danych
- Lombok oraz MapStruct
- Swagger/OpenAPI do dokumentacji API
- Docker 

Wymagania
---------
- Java JDK 17
- Maven
- Docker (opcjonalnie, do uruchomienia aplikacji w kontenerach)

Instrukcje uruchomienia
-----------------------
1. Uruchomienie lokalne
   a) Klonowanie i budowanie projektu:
    1. Sklonuj repozytorium:
       git clone <adres_repozytorium>
       cd complaint-management-api
    2. Zbuduj projekt:
       mvn clean package
       b) Uruchomienie aplikacji:
    - Uruchomienie przez Maven:
      mvn spring-boot:run
    - Lub uruchomienie wygenerowanego jar’a:
      java -jar target/complaint-management-api-*.jar
      Aplikacja będzie dostępna pod adresem: http://localhost:8080

2. Uruchomienie przez Docker Compose
    1. Upewnij się, że masz zainstalowany Docker oraz Docker Compose.
    2. W katalogu głównym projektu uruchom:
       docker-compose up --build
       Kontenery:
        - database – PostgreSQL
        - app – aplikacja
    3. Aplikacja dostępna pod adresem: http://localhost:8080

3. Konfiguracja bazy danych i migracje Liquibase
    - Baza danych: ustawienia w pliku src/main/resources/application.yml
    - Migracje: Liquibase uruchomi migracje wg pliku:
      src/main/resources/db/changelog/db.changelog-master.xml

4. Testowanie
    - Dokumentacja API (Swagger/OpenAPI):
      http://localhost:8080/swagger-ui/index.html

