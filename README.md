# Distributed_casino_project
ergasia katanemimena 2026


Το project υλοποιεί ένα κατανεμημένο σύστημα καζίνο σε Java με Maven. Η επικοινωνία γίνεται αποκλειστικά μέσω TCP Sockets με ObjectOutputStream και ObjectInputStream. Η μοναδική εξωτερική εξάρτηση είναι η Gson 2.10.1 για φόρτωση παιχνιδιών από JSON.
Η αρχιτεκτονική βασίζεται σε πέντε τύπους κόμβων. Ο Manager είναι ένα απλό CLI για το διαχειριστή που ανοίγει νέο socket για κάθε αίτημα. Ο MasterServer είναι ο κεντρικός router του συστήματος, καθώς δέχεται όλες τις συνδέσεις και τις δρομολογεί στους Workers μέσω hash partitioning. Οι Workers κρατούν την in-memory κατάσταση και εκτελούν την επιχειρησιακή λογική. O ReducerServer συγκεντρώνει αποτελέσματα MapReduce από τους Workers. Τέλος, ο SRGServer παράγει τυχαίους αριθμούς με SHA-256 authentication για να εξασφαλιστεί η ακεραιότητ τους. 
Η λογική του παιχνιδιού (handlePlay) δουλεύει ως εξής: αφαιρείται το ποντάρισμα από το balance του παίκτη, λαμβάνεται τυχαίος αριθμός από το RandomNumberBuffer που τροφοδοτεί ο SRGClient, και υπολογίζεται ο πολλαπλασιαστής από τους πίνακες ρίσκου. Αν ο randomNumber % 100 == 0, ενεργοποιείται jackpot, αλλιώς ο δείκτης στον πίνακα είναι randomNumber % 10. Το τελικό αποτέλεσμα είναι betAmount * multiplier – betAmount.
Για το συγχρονισμό το σύστημα χρησιμοποιεί παντού Java monitors. To SearchState και το ReducerState υλοποιούν barriers με wait/notifyAll. Το RandomNumberBuffer είναι το κλασικό Producer-Consumer pattern με capacity 10. 

