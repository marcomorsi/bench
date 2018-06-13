Progetto Bench
==============

---Descrizione Progetto---

L'applicazione effettua un benchmark sul DB PostgreSQL. In particolare l'applicazione calcola i tempi di min/max/avg per INSERT statements gestiti in batch di dimensione fissa e configurabile dall'utente. Successivamente calcola i tempi di min/max/avg per SELECT statements utilizzando le Primary Key come filtro. Il DB su cui si appoggia è PostgreSQL 10.

Il main contentuto nella omonima classe istanzia un BanchManager per lanciare i tests.
In particolare il BanchManager apre la connessione al DB, crea una tabella di test (se già presente ne fa la drop), chiude la connessione e crea 2 istanze di Tester  (è l'esecutore vero e proprio dei test) uno per effettuare le Insert e uno per le Select (ho fatto due sotto classi concrete: InsertTester, SelectTester). Ad ogni tester istanziato, il BanchManager richiede l'esecuzione dei suoi test ed al termine stampa le statistiche (tempo min/max/avg per un batch di insert e per una singola select).
A sua volta un Tester apre la connessione al DB, genera il PreparedStatement opportuno e ad ogni esecuzione: setta i paramentri necessari, esegue lo statement, annota il tempo impiegato e controlla se il tempo impiegato può essere candidato come min o max. Ovviamente al termine di tutte le esecuzioni il Tester ha immediatamente il valore del tempo min/max, deve solo quindi calcolare il tempo avg e chiude la connessionel al DB.
I testers si appoggiano ad un DbHelper per le operazioni di connessione/disconnessione al DB e di generazione, parametrizzazione ed esecuzione dei PreparedStatement.
La classe ConfigHelper ha lo scopo di leggere il config.properties (presente in "src/main/resources") in cui sono presenti i paramentri per la connessione a PotgreSQL ed il numero di INSERT/SELECT.

Il primo tester gestisce le insert. Vengono eseguite in batch da N statements e la commit viene fatta al termine dal batch.
Nel file config.properties il paramentro numOfInsertStatementsPerTransaction consente di definire il numero di statement per batch.
Venogono fatte M esecuzioni del batch e poi si generano le statistiche (min, max, avg). 
Nel file config.properties il paramentro numOfBatchInsertExecutions consente di definire il numero di esecuzioni del batch.

Dopo le insert, il secondo tester effettua Q select e poi si generano le statistiche (min, max, avg).   
In config.properties il paramentro numOfSelectExecutions consente di definire il numero di esecuzioni delle select.

---Compilazione e generazione del package---

mvn clean package

---Esecuzione del software---

java -cp target/bench-1.0-SNAPSHOT.jar;lib/* com.morsiani.bench.main.Main

---Esempio di output---

Running tester: INSERT STATEMENTS
------------------------------------------------------
Statitics
N. esecuzioni di insert-batch: 20000
N. insert in ogni batch: 10

tempo min (di insert di un batch di record): 879951 nsec
tempo max (di insert di un batch di record): 271559493 nsec
tempo avg (di insert di un batch di record): 1322770 nsec
tempo di insert di un record nel batch con tempo min: 87995 nsec
tempo di insert di un record nel batch con tempo max: 27155949 nsec
tempo di insert di un record nel batch con tempo avg: 132277 nsec
######################################################


Running tester: SELECT STATEMENTS
------------------------------------------------------
Statitics
N. esecuzioni: 100000

tempo min: 83331 nsec
tempo max: 130293070 nsec
tempo avg: 98190 nsec
######################################################
