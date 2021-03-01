# catering_management_javafx
## Cat&Ring 2019-2020
Cat & Ring si propone come un’applicazione che permette di gestire una società di catering  in tutti i suoi aspetti: organizzazione eventi e cucina.
Introduzione 
La società di catering si occupa di fornire un servizio di pranzo/cena/aperitivo/buffet/coffee break nel contesto di eventi sociali o aziendali.
Essa arruola diverse figure: al livello più alto ci sono gli organizzatori, che gestiscono il personale e gli eventi; ci sono poi gli chef che stabiliscono i menu e supervisionano la cucina; i cuochi, che preparano il cibo, il personale di servizio, che si occupa del servizio durante l’evento stesso, e non si esclude di voler aggiungere in futuro altre figure. 
Cat & Ring dovrà permettere agli organizzatori di dettagliare gli eventi e richiedere il personale che serve per realizzarli, assegnando a ciascuno dei compiti specifici. Inoltre dovranno supervisionare le attività (quindi vedere i dettagli di tutti gli eventi attualmente in corso o terminati) ed inserire i dati del personale. Il personale dovrà poi inserire le proprie disponibilità nei turni definiti dall’organizzatore. Gli chef e i cuochi dovranno  gestire un ricettario, creandone le ricette e definendo i menù da usare per i diversi eventi. In particolare, gli chef possono: gestire un ricettario, creare le ricette e definire i menù da usare per i diversi eventi, mentre i cuochi possono solo gestire il ricettario e creare le ricette, ma 
NON possono definire i menù da usare per gli eventi.

##Gli eventi
Un evento può essere semplice, e prevedere un singolo servizio (ad esempio un pranzo di matrimonio, una cena di festeggiamento, un buffet aziendale) o può essere più complesso, ad esempio durare un giorno intero o più giorni, e prevedere per ciascuna giornata più servizi (pranzo e cena, colazione e pranzo, coffee-break mattino e pomeriggio, ecc). Ciascun servizio avrà una precisa fascia oraria, e naturalmente un proprio menu e un proprio staff di supporto. 
Gli eventi possono essere classificati come ricorrenti nel caso in cui si ripetano con una certa regolarità. Nel caso si organizzi un evento ricorrente (es. annuale) si deve tener traccia dei menù precedenti in modo da differenziare l’offerta dei piatti proposti o, se lo si desidera, ripeterla tale e quale. Per ogni evento si deve anche tener traccia del cliente che lo ha commissionato.
Ciascun evento o giornata di un evento, dal punto di vista della società di catering, prevede due momenti diversi di lavoro: il lavoro preparatorio che si svolge in sede, e il servizio, che si svolge nel luogo dell’evento, e che può andare dal semplice buffet all’allestimento di una vera e propria sala ristorante. 


## I turni
Gli organizzatori e gli chef non hanno particolari turni, mentre le figure restanti hanno turni ben definiti, in quanto non ci si aspetta che lavorino a tempo pieno per la società.
Per ogni giorno sono previsti diversi turni di lavoro, definiti dall’organizzatore in sede di configurazione: si dividono in turni preparatori in sede  (ad es: mattina, 7.30-12.30, e pomeriggio, 14-19) e turni di servizio prima dell’evento stesso e che prevedono sempre un tempo di briefing prima dell’evento stesso, il tempo di allestimento iniziale e il tempo di pulizie al termine (ad es: morning break, 8.30-12; pranzo, 11.30-15.30, afternoon break 15.00-17.30, aperitivo 17.30-21.30, cena 18.30-23.30) che si svolgono sul luogo dell’evento. 
Il personale potrà indicare la propria disponibilità per i diversi turni e/o giorni (a seconda della tipologia di personale: il personale di servizio solo nei turni di servizio, mentre i cuochi possono essere disponibili in entrambe le situazioni in quanto anche durante il servizio possono essere necessarie attività di cucina). 
I cuochi possono essere chiamati per un turno di cucina dagli chef che la gestiscono. Il personale di sala e gli stessi cuochi possono essere chiamati per un turno di servizio dall’organizzatore di un evento. Per quanto riguarda i turni di cucina, uno chef potrà contrassegnare  un turno come “completo” qualora ritenesse che non c’è spazio o attrezzatura per aggiungere altre attività. A quel punto non sarà più possibile chiamare cuochi in quel turno.
### 2. Organizzazione di un evento
Quando arriva una richiesta per un evento, uno degli organizzatori se ne fa carico. Egli dovrà creare una scheda per l’evento (specificando luogo, date, tipo di servizio per le varie giornate, numero di persone, ed eventuali note particolari), e affidare ciò che riguarda la cucina ad uno chef. A quel punto l’organizzatore segue la gestione  del servizio durante l’evento, mentre lo  chef è responsabile della preparazione delle ricette in sede. 

Per quanto riguarda il servizio durante l’evento l’organizzatore dovrà scegliere il personale di servizio per ogni turno di servizio associato all’evento, indicando il ruolo che avrà in quella particolare situazione (es. Mario→servire le bevande, Luisa→girare in sala offrendo finger food). Se nel menù servito in quel turno ci sono ricette che prevedono passaggi non banali da svolgere all’ultimo, l’organizzatore può anche decidere di assegnare un  cuoco a quel particolare servizio (non necessariamente  in tutti i servizi di quell’evento). 

Lo chef dal canto suo dovrà individuare uno o più menù adeguati per l’evento; può trattarsi di menù già esistenti (ad esempio usati in eventi precedenti), o menù che lo chef compone per l’occasione. L’approvazione dei menù da parte dell’organizzatore dà il via ai lavori, a quel punto l’evento è “in corso”  e non può più essere eliminato, ma solo eventualmente annullato. Sarà ancora possibile tuttavia modificarne alcune caratteristiche.

Prima di approvare i menù l’organizzatore può proporre delle modifiche ai menù, suggerendo piatti da aggiungere o togliere, ovviamente questo non modifica i menù originali scelti dagli chef; queste proposte restano visibili come aggiunte o eliminazioni limitate all’evento in questione. Lo chef potrà decidere se “tenere” le proposte dell’organizzatore o rimuoverle. 

Al termine di un evento l’organizzatore lo “chiude” aggiungendo eventuali note e allegando documentazione rilevante. 
### 3. Assegnamento dei compiti di cucina
Per quanto riguarda la preparazione del cibo in sede, è lo chef ad assegnare i compiti ai cuochi nei diversi turni di preparazione. I compiti includono la realizzazione dei preparati intermedi e delle ricette finali. Più cuochi possono lavorare alla stessa ricetta, ad esempio preparando ciascuno una parte delle porzioni richieste. Non è invece previsto che più cuochi si dividano la procedura da realizzare “verticalmente” (ossia facendo ciascuno solo alcune preparazioni) perché in tal caso ci si aspetta invece che la procedura venga suddivisa a livello di ricettario in preparazioni separate.
Ad esempio: se ci sono da fare 10 teglie di lasagne, è possibile affidarne 5 a un cuoco e 5 ad un altro, ma non è possibile affidare la preparazione della sfoglia a un cuoco e la preparazione del ragù ad un altro, a meno che sfoglia all’uovo e ragù non siano due preparazioni distinte nel ricettario. Quindi se si vogliono dividere i compiti in questo modo, nel ricettario ci dovranno essere tre preparazioni, “sfoglia all’uovo”, “ragù” e “lasagne”, e la ricetta delle lasagne dovrà prevedere come ingredienti la sfoglia all’uovo e il ragù. A quel punto se nel menù ci sono le lasagne, lo chef si troverà a dover assegnare tutte e tre le preparazioni, e potrà assegnarle a persone diverse. Se invece nel ricettario c’è solo la ricetta delle lasagne, lo chef potrà solo assegnarla tutta intera.

Quando assegna un’attività, lo chef deve anche dare (sfruttando le informazioni che accompagnano la ricetta, si veda la sezione relativa) una stima del tempo che l’attività richiede. Poiché un cuoco può svolgere più attività nello stesso turno, è possibile assegnargli un’attività solo se il tempo a sua disposizione glielo permette.

Lo chef e l’organizzatore possono inoltre monitorare lo svolgimento delle attività perché i cuochi, man mano che portano a termine un compito, contrassegnano la ricetta o procedura come “completata”. In questo modo chef e organizzatore possono verificare che tutto stia procedendo come deve ed operare eventuali aggiustamenti in corsa.

### 4. Ricette e Preparazioni
Il ricettario contiene ricette e preparazioni; si tratta di concetti molto simili, la differenza è che una ricetta descrive come preparare un piatto da servire a tavola, mentre una preparazione descrive come realizzare un preparato da utilizzare in un’altra. 

Chef e cuochi possono inserire ricette o preparazioni nel ricettario; solo il proprietario di una ricetta o preparazione  (chi la ha inserita) può però eliminarla o modificarla, e può farlo solo fintanto che la ricetta non è in uso in alcun menù. Se un utente vuole modificare una propria ricetta attualmente in uso, o una ricetta di un altro proprietario, può crearne una copia da modificare liberamente.

Una ricetta o preparazione è innanzitutto caratterizzata da un nome, da un proprietario (chi l’ha inserita), opzionalmente da un autore (chi l’ha ideata inizialmente), e può essere accompagnata da una descrizione breve di ciò che realizza o da altre note che si ritiene possano essere di interesse. 
Gli utenti desiderano poter associare alle ricette tag scelti da loro allo scopo di organizzarle e reperirle con maggior facilità (esempi di tag: crudo, vegetariano, finger food, dessert, pasta).

Poiché per organizzare il lavoro è importante sapere quanto tempo ci vuole a cucinare qualcosa, chi scrive la ricetta o preparazione dovrà anche dare informazioni su diverse tempistiche:
il tempo di attività concreta (TAC) richiesto a chi la prepara. Per definire questo tempo si dovranno specificare due fattori: il tempo costante (TACc), che non dipende dal numero di porzioni, e il tempo variabile (TACv), che dipende invece dal numero di porzioni. Ad esempio preparare le tagliatelle ha un TACc di 15 minuti (la miscelatura dell’impasto a macchina) e un TACv di 1 minuto (realizzazione della pasta, sempre a macchina).
il tempo totale di preparazione (TT), ossia quanto passa dal momento in cui si inizia a svolgere la ricetta al momento in cui può essere servita (o utilizzata, nel caso di una preparazione). Questo include anche quelli che per il cuoco sono tempi morti: raffreddamenti in frigo, cotture lente,  riposo di impasti, ecc.
il tempo di ultimazione (TU), ossia quanto tempo serve in fase di servizio per ultimare il piatto; rientrano in questa categoria tutte le cose che devono essere fatte all’ultimo, che possono andare dal semplice impiattamento nel caso di piatti freddi, alla cottura e condimento della pasta, al taglio di un arrosto, ecc. Anche qui avremo una suddivisione in due fattori costante (TUc), indipendente dal numero di porzioni, e variabile (TUv), dipendente dal numero di porzioni. Ad esempio la realizzazione degli spaghetti al ragù ha un TUc di 10 minuti (cottura della pasta) e un TUv di 30 secondi (impiattamento col sugo). 

Per ogni ricetta o preparazione andranno poi specificati gli ingredienti. Gli ingredienti potranno essere ingredienti di base, scelti da un elenco che si immagina predefinito nel software e che dovrà essere il più possibile esaustivo, oppure preparati ottenuti tramite altre preparazioni. 
Degli ingredienti si dovrà poter specificare la dose. Inoltre, chi scrive la ricetta dovrà indicare con quelle dosi quante porzioni si realizzano o quale quantità di preparato risulterà. 

Naturalmente una ricetta o preparazione non sarebbe tale senza le istruzioni! In Cat & Ring le istruzioni di una ricetta o preparazione sono sempre divise in due sezioni, la parte che può essere realizzata in anticipo e quella che deve essere realizzata all’ultimo sul posto dell’evento. Naturalmente è possibile che una delle due sezioni sia vuota. 

Le istruzioni sono strutturate in una sequenza e possono essere di diverse tipologie:
istruzione semplice: un singolo passaggio di svolgimento; chi scrive la ricetta ha libertà di inserire un qualsivoglia testo, ma l’invito è a separare i passaggi che corrispondono ad attività  distinte in istruzioni differenti.
raggruppamento: una sequenza di istruzioni che vengono uno dopo l’altro e sono raggruppate per ragioni di leggibilità
variante: formata da un’istruzione (che può essere semplice, o un raggruppamento) “principale” e da una “variante” (un’altra istruzione, anche qui semplice o raggruppamento). 
ripetizione: un’istruzione (semplice o raggruppamento) con l’indicazione di una regola di ripetizione
L’utente è anche responsabile di indicare, quando inserisce un’istruzione, dove si situa rispetto alle istruzioni già presenti, affinché sia chiara la sequenza. 

Estrapolazione di preparazioni
Può succedere che si voglia “scorporare” una parte di una ricetta in una preparazione separata. Dovrà allora essere possibile per l’utente scegliere una sotto-sequenza di istruzioni della sua ricetta e un sottoinsieme di ingredienti e dire di creare, a partire da essi, una preparazione.
Questo avrà due effetti automatici:
Creare una nuova preparazione con le istruzioni e gli ingredienti scelti. L’utente dovrà poi completare le informazioni mancanti.
Rimuovere le istruzioni scelte dalla ricetta corrente, diminuisce in modo opportuno le dosi (o elimina del tutto se necessario) degli ingredienti spostati, e inserire la nuova preparazione fra gli ingredienti di quella ricetta.

### 5. I menù
Lo chef costruisce i suoi menù a partire dalle ricette nel ricettario. Un menù si compone di diverse voci, opzionalmente divise in diverse sezioni (potrebbe anche esserci una sezione sola corrispondente all’intero menù). 
Ogni voce fa riferimento ad una ricetta nel ricettario, ma il testo della voce può anche essere diverso dal nome della ricetta (ad esempio, la ricetta potrebbe chiamarsi “Vitello tonnato” mentre la voce di menu essere “girello di fassone con salsa tonnata”).

Un menù è anche caratterizzato da informazioni aggiuntive, quali:
se è consigliata la presenza di un cuoco durante il servizio per finalizzare le preparazioni
se prevede solo piatti freddi o anche piatti caldi
se richiede la disponibilità di una cucina nella sede dell’evento
se è adeguato per un buffet
se può essere fruito senza posate (finger food)

Lo chef può modificare i suoi menù liberamente fintanto che non sono utilizzati in nessun evento. Nel momento in cui un menù viene utilizzato per un evento non può più essere modificato, sarà però possibile crearne uno nuovo partendo da una copia di quello esistente. Lo stesso avviene se lo chef desidera creare un menù a partire da uno esistente fatto da un altro chef.
### 6. Esempi di eventi per cui potrebbe essere richiesto il catering
Esempi di eventi singoli
pranzo di laurea 1 menù
aperitivo aziendale  1 menù
Esempi di eventi complessi
1) singola giornata composta da più eventi
matrimonio: aperitivo + cena 2 menù
fiera aziendale: pranzo + 2 coffee break 2 o più menù
2) evento che si sviluppa in più giornate con le stesse caratteristiche
3 giorni di conferenza nella quale vengono offerti pranzo + 2 coffee break al giorno
2 o più menù al giorno, i menù possono variare con i giorni
3) evento che si sviluppa in più giornate con caratteristiche diverse
3 giorni di fiera aziendale 
giorno 1 pranzo + coffee break  (pomeriggio)
giorno 2 pranzo + 2 coffee break  (mattina e pomeriggio) + aperitivo
giorno 3 pranzo + coffee break  (mattina) 

a questo evento sono associati piu’ menù, alcuni di essi possono essere usati piu’ volte (es il coffee break )
Esempi di eventi ricorrenti
Evento singolo ricorrente: 
cena aziendale annuale
aperitivo promozionale organizzato ogni 3 mesi 

Eventi complessi ricorrenti
3 giorni di conferenza con cadenza annuale nella quale vengono offerti pranzo + 2 coffee break al giorno
2 o più menù al giorno, i menù possono variare con i giorni e di anno in anno. 
3 giorni di showroom aperto ai compratori ogni 6 mesi
giorno 1 pranzo + coffee break  (pomeriggio)
giorno 2 pranzo + 2 coffee break  (mattina e pomeriggio) + aperitivo
giorno 3 pranzo + coffee break  (mattina) 

a questo evento sono associati più menù, alcuni di essi possono essere usati più volte (es il coffee break). In generale i menù possono variare con i giorni e di anno in anno. 



