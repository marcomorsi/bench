Descrizione Progetto

L'applicazione effettua dei benchmark sul DB PostgreSQL.

Il main contentuto nella omonima classe istanzia un BanchManager per lanciare i tests.
In particolare il BanchManager apre la connessione al DB, crea la tabella di test (se presente ne fa una drop), chiude la connessione e crea 2 istanze di Tester  (è l'esecutore dei test) uno per le Insert e uno per le Select (ho fatto due sotto classi concrete: InsertTester, SelectTester). Ad ogni tester istanziato, il BanchManager richiede l'esecuzione dei suoi test ed al termine stampa le statistiche (tempo min/max/avg di un batch di insert e di una singola select).
A sua volta un Tester apre la connessione al DB, genera il PreparedStatement opportuno e ad ogni esecuzione: setta i paramentri necessari, esegue lo statement, annota il tempo impiegato e controlla se il tempo impiegato può essere candidato come min o max. Ovviamente al termine di tutte le esecuzioni il Tester ha immediatamente il valore del tempo min/max, deve solo quindi calcolare il tempo avg e chiude la connessionel al DB.
I testes si appoggiano ad un DbHelper per le operazioni elementari di connessione/disconnessione al DB, geenrazione, paramentrizzazione ed esecuzione di PreparedStatement.
La classe ConfigHelper ha lo scopo di leggere il config.properties (presente in "src/main/resources")in cui sono presenti i paramentri per la connessione a PotgreSQL ed il numero di INSERT/SELECT.

Le insert vengono eseguite in batch da N statements e la commit viene fatta al termine dal batch.
In config.properties il paramentro numOfInsertStatementsPerTransaction consente di definire il numero di statement per batch.
Venogono fatte M esecuzioni del batch e poi si geenrano le statistiche (min, max, avg). 
In config.properties il paramentro numOfBatchInsertExecutions consente di definire il numero di esecuzioni del batch.

Dopo le insert, vengono effettuate Q select e poi si generano le statistiche (min, max, avg).   
In config.properties il paramentro numOfSelectExecutions consente di definire il numero di esecuzioni delle select.

Per compilare e generare il package del software:
mvn clean package

Per lanciare il software:

java -cp target/bench-1.0-SNAPSHOT.jar;lib/* com.morsiani.bench.main.Main

Esempio di output:

Running tester: INSERT STATEMENTS
------------------------------------------------------
Statitics
Batch Insert Executions: 20000
N. inserts in each batch: 10

min (to insert a batch of records): 883137 nsec
max (to insert a batch of records): 111099587 nsec
avg (to insert a batch of records): 1113992 nsec
time per record in the batch with min time: 88313 nsec
time per record in the batch with max time: 11109958 nsec
avg (to insert a record): 111399 nsec
######################################################


Running tester: SELECT STATEMENTS
------------------------------------------------------
Statitics
Executions: 100000

min: 85144 nsec
max: 13094437 nsec
avg: 96621 nsec
######################################################