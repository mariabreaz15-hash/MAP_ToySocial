METODE AVANSATE DE PROGRAMARE – TOY SOCIAL NETWORK
Laboratoarele 3 si 4 – persistenta, factory, exceptii si refactorizare Java 8
============================================================================================================================================================

1.DESCRIEREA PROIECTULUI
------------------------------------------------------------------------------------------------------------------------------------------------------------
Aplicatia gestioneaza utilizatori si relatii de prietenii, permite operatii CRUD si analizeaza comunitatile din reteaua sociala.


2.FUNCTIONALITATI
------------------------------------------------------------------------------------------------------------------------------------------------------------

Administrarea utilizatorilor:
Adaugarea/Stergerea/Modificarea unui utilizator. Afisarea tuturor utilizatorilor.

Administrarea prieteniilor:
Adaugarea/Stergerea unei prietenii intre doi utilizatori existenti. Afisarea prieteniilor.
Memorarea datei si orei asociate unei prietenii. Stergerea prieteniilor asociate unui utilizator atunci cand acesta este eliminat.

Analiza retelei sociale:
Calcularea numarului de comunitati(componente conexe), a comunitatii unui utilizator. Determinarea celei mai sociabile comunitati,pe baza structurii grafului de prietenii(comunitatea cu diametrul maxim).
Sortarea utilizatorilor dupa nume si prenume; filtrarea lor prin Predicate.


3.ARHITECTURA
------------------------------------------------------------------------------------------------------------------------------------------------------------

Pachetul domain
Entity<ID> - clasa generica de baza, cu dentificator.
User - reprezinta un utilizator, identificat prin ID, prenume si nume.
Friendship - reprezinta relatia de prietenie dintre doi utilizatori si contine data prieteniei.
Tuple<A,B> - pereche generica.

Pachetul repository
Repository<ID,E> - contractul CRUD si filtrarea implicita cu Predicate.
InMemoryRepository<ID,E> - salvare intr-un Map, validare la salvare/modificare.
AbstractFileRepository<ID,E> - Template Method pentru citirea si scrierea fisierelor.
UtilizatorFileRepository - transforma utilizatorii din si in format text.
PrietenieFileRepository - parsarea si serializarea prieteniilor in format text.
RepositoryFactory - alege implementarea pe baza configuratiei.
RepositoryType - enumerare care defineste tipurile MEMORY, FILE si DATABASE.
UtilizatorDbRepository si PrietenieDbRepository - schelete pentru o implementare viitoare a bazei de date.

Pachetul validation
Validator<E> - interfata generica pentru validare.
UserValidator - verifica validitatea datelor unui utilizator.
FriendshipValidator - reguli pentru prietenie si existenta ambilor utilizatori.

Pachetul Service
SocialNetworkService - logica de business si algoritmii de graf.
CriteriiUtilizator - fabrici de Predicate<User> pentru filtrare.

Pachetul ui
ConsoleUI - gestioneaza interactiunea cu utilizatorul prin intermediul consolei

Pachetul exceptions
ValidationException,RepositoryException,ServiceException.

Main - incarca configruatia, compune validatorii, creeaza repository-urile si porneste aplicatia.


4.REPOSITORY GENERIC, OPTIONAL SI PREDICATE
------------------------------------------------------------------------------------------------------------------------------------------------------------
Contractul Repository<ID,E> defineste:
save(E entity), findOne(ID id), findAll(), delete(ID id), update(E entity)
findAll(Predicate<E filter>) - este o metoda default care aplica findAll().stream().filter(filter).collect(Collectors.toList()).

In service criteriile se compun cu and(),or() si negate(). Negarea este intentionata pentru demonstrarea API-ului.


5.PPERSISTENTA SI FORMATELE DE FISIER
------------------------------------------------------------------------------------------------------------------------------------------------------------
Configuratia stabileste caile fisierelor.

Fisierul utilizatorilor: data/utilizatori.txt (id;prenume;nume) (ex. 1;Ana;Popescu)
Fisierul prieteniilor: data/prietenii.txt (idUtilizator1;idUtilizator2;dataPrieteniei) (ex. 1;2;2026-10-20T14:30:00)
La initializarea repository-urilor, datele existente sunt incarcate din fisiere.
Citirea si scrierea se realizeaza folosind codificarea UTF_8 si mecanismul try-with-resources.


6.CONFIGURARE SI RULARE
------------------------------------------------------------------------------------------------------------------------------------------------------------

src/main/resources/config.properties
repository.type=FILE
file.utilizatori=data/utilizatori.txt
file.prietenii=data/prietenii.txt
MEMORY - datele sunt pastrate doar in memorie.
FILE - datele sunt pastrate in fisiere text.
DATABASE - optiune prevazuta in arhitectura, dar neimplementata functional.
Clasa RepositoryFactory citeste configuratia si creeaza implementarile corespunzatoare fara ca restul aplicatiei sa depinda direct de tipul de persistenta.
EnumMap si Supplier sunt folosite pentru construirea lazy a repository-urilor.


7.DESIGN PATTERNS UTILIZATE
------------------------------------------------------------------------------------------------------------------------------------------------------------

Simple Factory - RepositoryFactory
Clasa RepositoryFactory este responsabila pentru crearea repository-urilor in functie de configuratia aplicatiei (MEMORY, FILE, sau DATABASE). Expresiile lambda permit instantierea lazy, astfel incat este creat doar repository-ul selectat prin configuratie.

Template Method - AbstractFileRepository
Am pastrat sablonul Template Method din laboratorul 3, deoarece acesta permite reutilizarea operatiilor comune de citire si scriere a entitatilor in fisiere. Metodele extractEntity() si createEntityAsString() permit subclaselor sa stabileasca modul de transformare a liniilor de text in entitati si invers.
Am ales sa pastrez acest sablon deoarece ofera reutilizarea codului, separa algoritmul general de particularitatile fiecarui tip de entitate si reduce duplicarea logicii de persistare.

Strategy - Repository si Validator
Interfata Repository permite utilizarea diferitelor strategii de stocare a datelor, fara modificarea logicii din SocialNetworkService. In Laboratorul 4, interfata Validator a fost definita ca interfata functionala, folosind adnotarea @FunctionalInterface.
Prin metoda andThen, mai intai este executata validarea curenta, apoi validarea suplimentara, daca prima s-a incheiat fara exceptii: Validator<User> userValidator = new UserValidator().andThen(additionalValidation);
Aceasta abordare permite adaugarea unor reguli noi de validare fara modificarea clasei UserValidator si demonstreaza utilizarea interfetelor functionale, a expresiilor lambda si a metodelor default in Java 8.

8.PRINCIPII SOLID
------------------------------------------------------------------------------------------------------------------------------------------------------------

S - Single Responsibility Principle
Entitatile modeleaza datele, Repository-urile gestioneaza persistenta, Interfata de consola gestioneaza interactiunea cu utilizatorul.

O - Open/Closed Principle
Arhitectura permite introducerea unor noi implementari de repository prin extinderea interfetelor si claselor abstracte existente, cu modificari limitate in codul aplicatiei.

L - Liskov Substitution Principle
Implementarile concrete ale repository-urilor pot fi utilizate prin intermediul interfeti Repository<ID,E>, respectand contractul operatiilor definit de aceasta.

I - Interface Segregation Principle
Interfetele Repository si Validator definesc operatii specifice responsabilitatilor lor, fara a obliga clasele care le implementeaza sa depinda de metode fara legatura cu rolul lor.

D - Dependency Inversion Principle
Dependentele sunt transmise prin constructor, ceea ce faciliteaza schimbarea implementarilor si testarea.


9.TRATAREA EXCEPTIILOR
------------------------------------------------------------------------------------------------------------------------------------------------------------
Aplicatia utilizeaza exceptii pentru semnalarea si gestionarea erorilor aparute in timpul validarii datelor, accesarii repository-urilor si executarii operatiilor de service.

ValidationException
Este aruncata de clasele UserValidator si FriendshipValidator atunci cand o entitate nu respecta regulile de validare (de ex. un utilizator are un prenume invalid, se incearca crearea unei prietenii intre un utilizator si el insusi). In Laboratorul 4, regulile de validare pot fi combinate folosind metoda default Validator.andThen(), iar exceptia este propagata daca una dintre validari esueaza.

RepositoryException
Exemple: Se incearca stergerea sau actualizarea unei entitati inexistente.
Apare o eroare de citire sau scriere a unui fisier.
Este utilizat mecanismul exception chaining, prin care exceptia initiala este pastrata drept cauza, folosind constructorul RepositoryException(message,cause).

ServiceException
Este aruncata in SocialNetworkService atunci cand operatia solicitata nu poate fi executata in conditiile stabilite (de ex. se incearca accesarea unui utilizator inexistent, se incearca stergerea unei prietenii inexistente, nu mai exista ID-uri disponibile pentru generarea unui utilizator nou).

UnsupportedOperationException
Metodele CRUD neimplementate arunca UnsupportedOperationException cu mesajul "Database repository is not implemented yet". RepositoryFactory poate selecta tipul DATABASE, dar executarea unei operatii neimplementate determina aparitia acestei exceptii.

Gestionarea exceptiilor in interfata aplicatiei
Sunt tratate NumberFormatException, ValidationException, RepositoryException, ServiceException si IllegalArgumentException. Exceptiile care nu sunt interceptate, precum in cazul unui fisier corupt, pot determina oprirea aplicatiei si afisarea stack-trace-ului.
UnsupportedOperationException este interceptata in ConsoleUI, fiind afisat un mesaj care indica faptul ca operatia nu este implementata.


10. TESTAREA FISIERELOR CORUPTE
------------------------------------------------------------------------------------------------------------------------------------------------------------
Pentru verificarea mecanismului de tratare a erorilor de persistenta, am realizat un test folosind un fisier care contine date invalide:
data/utilizatori_corupt_test.txt
Pasii de testare:
a)Am introdus in fisier utilizatori valizi si o inregistrare invalida, in care identificatorul utilizatorului nu este numeric.
b)Am modificat temporar fisierul config.properties, setand:
file.utilizatori=data/utilizatori_corupt_test.txt
c)La initializarea repository-ului, aplicatia a incercat sa citeasca si sa transforme fiecare linie din fisier intr-un obiect User.
d)Eroarea a fost semnalata prin RepositoryException, fiind indicata linia invalida si pastrata cauza initiala prin exception chaining:
Invalid line 2 in data/utilizatori_corupt_test.txt: Invalid user Id in line: abc;Maria;Ionescu
Aplicatia a indetificat inregistrarea invalida si a semnalat eroarea, fara sa o ignore in mod silentios. Testul demonstreaza functionarea mecanismului de validare a datelor citite din fisiere si utilizarea exceptiilor personalizate pentru raportarea erorilor de peristenta.


11. RULAREA APLICATIEI
------------------------------------------------------------------------------------------------------------------------------------------------------------
Aplicatia poate fi rulata in IntelliJ IDEA, prin executarea metodei main() din clasa Main. Caile relative sunt interpretate in raport cu directorul de lucru al aplicatiei, care trebuie configurat corespunzator in IntelliJ IDEA.
a)Se deschide proiectul in IntelliJ IDEA. Se configureaza tipul de repository dorit prin proprietatea repository.type.
b)Se deschide clasa Main.java si se executa metoda main(), folosind optiunea Run 'Main.main()'.

11.1 Rularea cu MEMORY
repository.type=MEMORY
Operatiile de adaugare, stergere, modificare, afisare si analiza a comunitatilor pot fi executate prin meniul consolei. Pentru verificare, se poate adauga un utilizator, apoi se poate reporni aplicatia.

11.2 Rularea cu FILE
repository.type=FILE
file.utilizatori=data/utilizatori.txt
file.prietenii=data/prietenii.txt
La pornirea aplicatiei, datele existente sunt incarcate din fisiere. Pentru verificare:
Se adauga un utilizator sau o prietenie. Se inchide aplicatia si se verifica in fisiere existenta datelor adaugate anterior.

11.3 Rularea cu DATABASE
repository.type=DATABASE
Clasele sunt implementate doar ca schelete, conform cerintei laboratorului, fara o conexiune functionala la o baza de date: "Database repository is not implemented yet".
Se seteaza repository.type=DATABASE. La executarea unei operatii care acceseaza repository-ul, este aruncata UnsupportedOperationException.
In implementarea actuala, daca aceasta exceptie este interceptata in ConsoleUI, executia aplicatie nu se opreste.

11.4 Revenirea la configuratia initiala
repository.type=FILE
file.utilizatori=data/utilizatori.txt
file.prietenii=data/prietenii.txt


12. EXEMPLE DE REFACTORIZARE BEFORE/AFTER - LABORATORUL 4
------------------------------------------------------------------------------------------------------------------------------------------------------------
Exemplul 1 - Determinarea ID-ului maxim al utilizatorilor
Metoda: addUser()
long maxId = 0;
for (User user : users.findAll()) {
    if (user.getId() > maxId) {
        maxId = user.getId();
    }
}

VS

long maxId = users.findAll().stream()
        .map(User::getId)
        .max(Long::compareTo)
        .orElse(0L);
Am inlocuit bucla for cu un pipeline.Stream.
Este utilizata si o refereinta la metoda User::getId.

Exemplul 2 - Stergerea prieteniilor asociate unui utilizator
Metoda: removeUser()
List<Tuple<Long, Long>> ids = new ArrayList<>();

for (Friendship friendship : friendships.findAll()) {
    if (friendship.getFirstUserId().equals(id)
            || friendship.getSecondUserId().equals(id)) {
        ids.add(friendship.getId());
    }
}

for (Tuple<Long, Long> friendshipId : ids) {
    friendships.delete(friendshipId);
}

VS

friendships.findAll().stream()
        .filter(f -> f.getFirstUserId().equals(id)
                || f.getSecondUserId().equals(id))
        .map(Friendship::getId)
        .collect(Collectors.toList())
        .forEach(friendships::delete);
Operația filter() selectează prieteniile care implică utilizatorul eliminat, map() extrage identificatorii acestora, iar collect() creează o listă intermediară.
Operația forEach() șterge fiecare prietenie selectată, utilizând referința la metodă friendships::delete.

Exemplul 3 – Determinarea celei mai sociabile comunități
Metoda: mostSociableCommunity()
Set<Long> best = new LinkedHashSet<>();

for (Set<Long> component : components(graph)) {
    if (best.isEmpty()
            || diameter(component, graph) > diameter(best, graph)) {
        best = component;
    }
}

VS

return components(graph).stream()
        .reduce((best, candidate) ->
                diameter(candidate, graph) > diameter(best, graph)
                        ? candidate : best)
        .map(LinkedHashSet::new)
        .orElseGet(LinkedHashSet::new);
Am înlocuit parcurgerea imperativă cu operația reduce(), care compară comunitățile pe baza diametrului grafului și o selectează pe cea cu diametrul maxim.
LinkedHashSet::new reprezintă o referință la constructor.

Exemplul 4 – Scrierea entităților într-un fișier
Metoda: writeToFile()
List<String> lines = new ArrayList<>();

for (E entity : findAll()) {
    lines.add(createEntityAsString(entity));
}

VS

List<String> lines = findAll().stream()
        .map(this::createEntityAsString)
        .collect(Collectors.toList());

Am înlocuit construirea imperativă a listei de linii cu operațiile map() și collect().
Referința this::createEntityAsString înlocuiește o expresie lambda echivalentă.

Exemplul 5 – Filtrarea utilizatorilor după un criteriu
List<User> result = new ArrayList<>();

for (User user : users.findAll()) {
    if (user.getLastName().equals(lastName)) {
        result.add(user);
    }
}

VS

default List<T> findAll(Predicate<T> filter) {
    return findAll().stream()
            .filter(filter)
            .collect(Collectors.toList());
}

Am introdus o metodă default în interfața Repository, care primește un Predicate<T> și filtrează entitățile folosind Stream API.

Exemplul 6 – Compunerea validatoarelor
UserValidator validator = new UserValidator();
validator.validate(user);

if (user.getFirstName().length() > 50) {
    throw new ValidationException("First name too long");
}

VS

Validator<User> additionalValidation = user -> {
    if (user.getFirstName().length() > 50) {
        throw new ValidationException("First name too long");
    }
};

Validator<User> userValidator =
        new UserValidator().andThen(additionalValidation);

Am introdus posibilitatea compunerii validatoarelor prin metoda default andThen() din interfața funcțională Validator.
Această abordare contribuie la respectarea principiului Open/Closed.



13. DECIZII DE PROIECTARE
------------------------------------------------------------------------------------------------------------------------------------------------------------
Justificarea pastrarii buclei while(true) in ConsoleUI: Aceasta permite afisarea repetata a meniului, citirea comenzilor utilizatorului si executarea operatiilor corespunzatoare pana la selectarea optiunii de iesire(0).
In schimb, parcurgerile colectiilor de utilizatori si prietenii au fost refactorizate folosind forEach() si referinte la metode, de exemplu service.getFriendships().forEach(System.out::println)


14. VERSIONARE GIT
------------------------------------------------------------------------------------------------------------------------------------------------------------
Proiectul este versionat local cu Git. Istoricul poate fi consultat cu:
  git log --oneline
  git --no-pager show --stat HEAD
  git diff <commit_vechi> <commit_nou>
Comparatiile before/after documenteaza trecerea de la Laboratorul 3 la Laboratorul 4.


15.LIMITARI SI OBSERVATII
------------------------------------------------------------------------------------------------------------------------------------------------------------
DATABASE este doar un schelet, fara implementare CRUD functionala. Algoritmii recursivi pot fi nepotriviti pentru grafuri foarte mari.


16. LABORATORUL 5 - PERSISTENTA POSTGRESQL PRIN JDBC
------------------------------------------------------------------------------------------------------------------------------------------------------------
a) Structura bazei de date:
Baza de date contine doua tabele:
	-> Tabelul users stocheaza informatii despre utilizatorii retelei sociale
	id - BIGINT, cheie primara (PRIMARY KEY), generata automat de PostgreSQL prin GENERATED ALWAYS AS IDENTITY.
	first_name si last_name - VARCHAR NOT NULL, reprezinta prenumele si numele utilizatorului
	Generarea automata a identificatorilor este realizata de baza de date, nu de nivelul Service.
	-> Tabelul friendships stocheaza relatiile de prietenie dintre utilizatori
	id1 si id2 - BIGINT indetificatorii primului si celui de-al doilea utilizator
	friends_from - TIMESTAMP NOT NULL, reprezinta data si ora inceperii prieteniei
	Coloanele id1 si id2 sunt chei externe (FOREIGN KEY) care refera coloana id din tabelul users.

b) Relatia dintre tabele:
Intre users si friendships: 1-M
Prin intermediul tabelului friendships este reprezentata o relatie de tip multi-la-multi (M) intre utilizatori.
Cheile externe sunt definite cu ON DELETE CASCADE, pentru ca stergerea unui utilizator sa determine automat stergerea prieteniilor sale.

c) Folosirea DriverManager, PreparedStatement si ResultSet:
In clasele UtilizatorDbRepository si PrietenieDbRepository sunt utilizate urmatoarele componente:
	->Clasa DriverManager este utilizata pentru stabilirea conexiunii dintre aplicatia Java si baza de date PostgreSQL.
	->Interfata PreparedStatement este utilizata pentru executarea instructiunilor SQL parametrizate, necesare operatiilor CRUD (INSERT, SELECT, UPDATE, 	DELETE).
	Utilizarea PreparedStatement permite separarea instructiunilor SQL de valorile introduse de utilizator si contribuie la prevenirea atacurilor de tip 	SQL Injection.
	->Interfata ResultSet este utilizata pentru prelucrarea rezultatelor obtinute in urma interogarilor SQL.
Datele extrase sunt utilizat pentru construirea obiectelor User si Friendship din aplicatie

d) Gestionarea resurselor si a erorilor
Excepțiile de tip SQLException sunt tratate și transformate în RepositoryException, pentru a separa erorile de acces la baza de date de logica aplicației.

e) Contractul metodelor save, update si delete
Metodele save(), update() si delete() au un contract comun, care trebuie respectat la toate implementarile repository-ului.
	-> Metoda save(E entity)
	Daca entitatea este null, se arunca IllegalArgumentException.
	Daca identificatorul exista deja, se arunca RepositoryException.
	Pentru utilizatorii noi, identificatorul este generat de repository.
	-> Metoda update(E entity)
	Daca entitatea este null, se arunca IllegalArgumentException.
	Daca identificatorul exista, entitatea este actualizata, iar metoda returneaza null.
	-> Metoda delete(ID id)
	Daca identificatorul este null, se arunca IllegalArgumentException.
	Metoda returneaza entitatea care a fost stearsa.

f) Gestionarea exceptiilor
Datele invalide determina aruncarea unei ValidationException.
Respectarea acestor contracte asigură un comportament unitar al aplicației, indiferent de tipul de persistență selectat prin repository.type din config.properties.

g) Testarea aplicatiei
	-> Testarea operatiilor CRUD
	Afisarea utilizatorilor existenti in baza de date.
	Ștergerea unui utilizator și verificarea eliminării acestuia din baza de date.
	Verificarea persistenței datelor după închiderea și repornirea aplicației.
	-> Testarea validarii datelor
	Incercarea de adaugare a unei prietenii intre un utilizator si el insusi.
	Verificarea comportamentului metodelor update() si delete() pentru identificatori inexistenti.
	-> Testarea cu serverul PostgreSQL oprit
	Aplicatia a fost rulata cu repository.type=DATABASE, apoi s-a incercat afisarea utilizatorilor.
	Rezultatul asteptat:
	Eroarea SQLException este capturata si transformata in RepositoryException.
	Aplicatia a afisat un mesaj de eroare controlat, fara afisarea unui stack trace complet.
	Dupa repornirea serverului PostgreSQL, se verifica reluarea functionarii aplicatiei.
	-> Testarea prevenirii SQL Injection
	In campul destinat prenumelui au fost introduse valorile:
	Robert'); DROP TABLE users; --
	Rezultatul asteptat:
	In urma testarii, valorile au fost tratate ca siruri de caractere obisnuite, fara modificarea sau executarea unor instructiuni SQL suplimentare.
	Instructiunea SQL executata nu este modificata de continutul introdus.
	Protectia impotriva SQL injection este realizata prin utilizarea PreparedStatement si a parametrilor ?, in locul concatenarii directe a valorilor in 	instructiunile SQL.
	Rezultatul confirmă utilizarea corectă a interogărilor parametrizate pentru operațiile testate și prevenirea SQL Injection prin câmpurile 	respective.
	-> Testarea schimbari tipului de Repository
	Se verifica functionarea operatiilor aplicatiei pentru fiecare implementare, fara modificarea codului din SocialNetworkService.

h) Scriptul SQL pentru crearea bazei de date
Pentru configurarea bazei de date, se creeaza mai intai baza de date socialnetwork, apoi se executa scriptul docs/schema.sql in pgAdmin Query Tool, fiind selectata baza de date socialnetwork.
Fișierul este inclus în proiect pentru a permite reproducerea structurii bazei de date pe un alt calculator.

