package com.example.progetto_software.database

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.progetto_software.data.* // Assicurati di avere tutte le classi data qui
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext // Import this for switching contexts
import java.time.LocalDateTime
import java.util.UUID

object DatabasePopulator {

    private const val TAG = "DatabasePopulator"

    private val valutazione_iniziale = 0.0f

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun populateDatabase(context: Context) {
        // Run database operations on the IO dispatcher
        withContext(Dispatchers.IO) {
            val db = AppDatabase.getDatabase(context)
            val filmDao = db.filmDao()
            val serieTvDao = db.serieTvDao()
            val episodioDao = db.episodioDao()

            Log.d(TAG, "Inizio popolamento database...")

            // --- Inserimento 7 Film (originali) ---
            val film1 = Film(
                id = UUID.randomUUID().toString(),
                trama = "Nel 1858, in Texas, uno schiavo liberato e un cacciatore di taglie tedesco si uniscono per dare la caccia a brutali fuorilegge.",
                nome = "Django Unchained",
                genere = "Western, Drammatico",
                eta = 16,
                valutazioneMedia = valutazione_iniziale,
                imageUrl = "django_unchained_poster",
                durataSecondi = (2 * 60 + 45) * 60L
            )
            val film2 = Film(
                id = UUID.randomUUID().toString(),
                trama = "Le vite di due sicari, un pugile, un gangster e sua moglie, e una coppia di rapinatori si intrecciano in quattro storie di violenza e redenzione.",
                nome = "Pulp Fiction",
                genere = "Crimine, Drammatico",
                eta = 18,
                valutazioneMedia = valutazione_iniziale,
                imageUrl = "pulp_fiction_poster",
                durataSecondi = (2 * 60 + 35) * 60L
            )
            val film3 = Film(
                id = UUID.randomUUID().toString(),
                trama = "Durante la Seconda Guerra Mondiale, un gruppo di soldati ebrei-americani, noto come 'Bastardi', viene incaricato di uccidere i leader nazisti.",
                nome = "Bastardi Senza Gloria",
                genere = "Guerra, Avventura",
                eta = 16,
                valutazioneMedia = valutazione_iniziale,
                imageUrl = "inglourious_basterds_poster",
                durataSecondi = (2 * 60 + 33) * 60L,
            )
            val film4 = Film(
                id = UUID.randomUUID().toString(),
                trama = "Un ricco uomo d'affari incontra una squillo di Hollywood e i due si innamorano, superando le barriere sociali.",
                nome = "Pretty Woman",
                genere = "Commedia Romantica",
                eta = 13,
                valutazioneMedia = valutazione_iniziale,
                imageUrl = "pretty_woman_poster",
                durataSecondi = (1 * 60 + 59) * 60L,
            )
            val film5 = Film(
                id = UUID.randomUUID().toString(),
                trama = "Saetta McQueen, un'auto da corsa ambiziosa, si perde sulla strada per una grande gara e scopre il vero significato dell'amicizia e della famiglia in una piccola città dimenticata.",
                nome = "Cars",
                genere = "Animazione, Avventura, Commedia",
                eta = 0,
                valutazioneMedia = valutazione_iniziale,
                imageUrl = "cars_poster",
                durataSecondi = (1 * 60 + 57) * 60L,
            )
            val film6 = Film(
                id = UUID.randomUUID().toString(),
                trama = "Stella Grant è una paziente affetta da fibrosi cistica che vive la sua vita tra ospedali e routine rigide. Quando incontra Will Newman, un altro paziente con la stessa malattia ma con un approccio più ribelle, si innamorano, ma devono mantenere una distanza di un metro e mezzo per non infettarsi a vicenda.",
                nome = "A un metro da te",
                genere = "Romantico, Drammatico",
                eta = 12,
                valutazioneMedia = valutazione_iniziale,
                imageUrl = "a_un_metro_da_te_poster",
                durataSecondi = (1 * 60 + 56) * 60L
            )
            val film7 = Film(
                id = UUID.randomUUID().toString(),
                trama = "August Pullman, un bambino con una grave deformità facciale, affronta per la prima volta la scuola pubblica, cercando di farsi accettare dai suoi compagni e dimostrare che la vera bellezza non è quella esteriore.",
                nome = "Wonder",
                genere = "Drammatico, Famiglia",
                eta = 6,
                valutazioneMedia = valutazione_iniziale,
                imageUrl = "wonder_poster",
                durataSecondi = (1 * 60 + 53) * 60L
            )

            filmDao.insertFilm(film1)
            filmDao.insertFilm(film2)
            filmDao.insertFilm(film3)
            filmDao.insertFilm(film4)
            filmDao.insertFilm(film5)
            filmDao.insertFilm(film6)
            filmDao.insertFilm(film7)

            // --- Nuovi Film Aggiunti ---
            val film8 = Film(
                id = UUID.randomUUID().toString(),
                trama = "Un ex sicario torna in azione per vendicare il furto della sua auto e l'uccisione del suo cane.",
                nome = "John Wick",
                genere = "Azione, Thriller",
                eta = 16,
                valutazioneMedia = valutazione_iniziale,
                imageUrl = "john_wick_poster",
                durataSecondi = (1 * 60 + 41) * 60L
            )
            val film9 = Film(
                id = UUID.randomUUID().toString(),
                trama = "Un gruppo di ladri esperti si unisce per compiere il colpo del secolo in un casinò di Las Vegas.",
                nome = "Ocean's Eleven",
                genere = "Crimine, Thriller, Commedia",
                eta = 10,
                valutazioneMedia = valutazione_iniziale,
                imageUrl = "oceans_eleven_poster",
                durataSecondi = (1 * 60 + 56) * 60L
            )
            val film10 = Film(
                id = UUID.randomUUID().toString(),
                trama = "Un archeologo avventuriero è incaricato dal governo americano di trovare l'Arca dell'Alleanza prima dei nazisti.",
                nome = "I predatori dell'arca perduta",
                genere = "Avventura, Azione",
                eta = 7,
                valutazioneMedia = valutazione_iniziale,
                imageUrl = "raiders_lost_ark_poster",
                durataSecondi = (1 * 60 + 55) * 60L
            )

            filmDao.insertFilm(film8)
            filmDao.insertFilm(film9)
            filmDao.insertFilm(film10)

            // --- Inserimento 3 Serie TV (originali) ---

            // Serie TV 1: Stranger Things
            val serieTv1 = SerieTv(
                id = UUID.randomUUID().toString(),
                trama = "Un gruppo di amici scopre segreti soprannaturali e esperimenti governativi.",
                nome = "Stranger Things",
                genere = "Horror, Sci-Fi",
                eta = 14,
                valutazioneMedia = valutazione_iniziale,
                imageUrl = "stranger_things_poster"
            )
            serieTvDao.insertSerieTv(serieTv1)
            Log.d(TAG, "Serie TV 1 inserita: ${serieTv1.nome}")

            val episodiSerie1 = listOf(
                Episodio(
                    id = UUID.randomUUID().toString(),
                    trama = "La scomparsa di Will Byers scuote la piccola città di Hawkins.",
                    nome = "Capitolo uno: La scomparsa di Will Byers",
                    genere = "Horror, Sci-Fi",
                    eta = 14,
                    valutazioneMedia = valutazione_iniziale,
                    imageUrl = serieTv1.imageUrl, // Usa l'immagine della serie TV
                    durataSecondi = 3000L,
                    idSerieDiAppartenenza = serieTv1.id
                ),
                Episodio(
                    id = UUID.randomUUID().toString(),
                    trama = "Dustin e Mike cercano di capire la ragazza con poteri.",
                    nome = "Capitolo due: La stramba di Maple Street",
                    genere = "Horror, Sci-Fi",
                    eta = 14,
                    valutazioneMedia = valutazione_iniziale,
                    imageUrl = serieTv1.imageUrl, // Usa l'immagine della serie TV
                    durataSecondi = 2800L,
                    idSerieDiAppartenenza = serieTv1.id
                ),
                Episodio(
                    id = UUID.randomUUID().toString(),
                    trama = "Joyce riceve strane chiamate; Hopper indaga sul laboratorio nazionale.",
                    nome = "Capitolo tre: Luci natalizie",
                    genere = "Horror, Sci-Fi",
                    eta = 14,
                    valutazioneMedia = valutazione_iniziale,
                    imageUrl = serieTv1.imageUrl,
                    durataSecondi = 3200L,
                    idSerieDiAppartenenza = serieTv1.id
                )
            )
            episodiSerie1.forEach { episodioDao.insertEpisodio(it) }
            Log.d(TAG, "Episodi per ${serieTv1.nome} inseriti.")


            // Serie TV 2: The Mandalorian
            val serieTv2 = SerieTv(
                id = UUID.randomUUID().toString(),
                trama = "Le avventure di un cacciatore di taglie solitario ai confini della galassia.",
                nome = "The Mandalorian",
                genere = "Sci-Fi, Avventura",
                eta = 10,
                valutazioneMedia = valutazione_iniziale,
                imageUrl = "the_mandalorian_poster"
            )
            serieTvDao.insertSerieTv(serieTv2)
            Log.d(TAG, "Serie TV 2 inserita: ${serieTv2.nome}")

            val episodiSerie2 = listOf(
                Episodio(
                    id = UUID.randomUUID().toString(),
                    trama = "Un cacciatore di taglie riceve un incarico misterioso.",
                    nome = "Capitolo 1: Il Mandaloriano",
                    genere = "Sci-Fi, Avventura",
                    eta = 10,
                    valutazioneMedia = valutazione_iniziale,
                    imageUrl = serieTv2.imageUrl, // Usa l'immagine della serie TV
                    durataSecondi = 2500L,
                    idSerieDiAppartenenza = serieTv2.id
                ),
                Episodio(
                    id = UUID.randomUUID().toString(),
                    trama = "Il Mandaloriano deve affrontare le conseguenze della sua ultima missione.",
                    nome = "Capitolo 2: Il Bambino",
                    genere = "Sci-Fi, Avventura",
                    eta = 10,
                    valutazioneMedia = valutazione_iniziale,
                    imageUrl = serieTv2.imageUrl, // Usa l'immagine della serie TV
                    durataSecondi = 2000L,
                    idSerieDiAppartenenza = serieTv2.id
                ),
                Episodio(
                    id = UUID.randomUUID().toString(),
                    trama = "Il Mandaloriano e il Bambino cercano rifugio e si imbattono in un'alleata inaspettata.",
                    nome = "Capitolo 3: Il Peccato",
                    genere = "Sci-Fi, Avventura",
                    eta = 10,
                    valutazioneMedia = valutazione_iniziale,
                    imageUrl = serieTv2.imageUrl,
                    durataSecondi = 2600L,
                    idSerieDiAppartenenza = serieTv2.id
                )
            )
            episodiSerie2.forEach { episodioDao.insertEpisodio(it) }
            Log.d(TAG, "Episodi per ${serieTv2.nome} inseriti.")


            // Serie TV 3: The Crown
            val serieTv3 = SerieTv(
                id = UUID.randomUUID().toString(),
                trama = "La storia della Regina Elisabetta II e degli eventi che hanno plasmato il suo regno.",
                nome = "The Crown",
                genere = "Dramma Storico",
                eta = 12,
                valutazioneMedia = valutazione_iniziale,
                imageUrl = "the_crown_poster"
            )
            serieTvDao.insertSerieTv(serieTv3)
            Log.d(TAG, "Serie TV 3 inserita: ${serieTv3.nome}")

            val episodiSerie3 = listOf(
                Episodio(
                    id = UUID.randomUUID().toString(),
                    trama = "Elisabetta sposa Filippo e il padre, Re Giorgio VI, è malato.",
                    nome = "Wolferton Splash",
                    genere = "Dramma Storico",
                    eta = 12,
                    valutazioneMedia = valutazione_iniziale,
                    imageUrl = serieTv3.imageUrl, // Usa l'immagine della serie TV
                    durataSecondi = 3600L,
                    idSerieDiAppartenenza = serieTv3.id
                ),
                Episodio(
                    id = UUID.randomUUID().toString(),
                    trama = "La salute del Re peggiora, costringendo Elisabetta ad assumere più responsabilità.",
                    nome = "Hyde Park Corner",
                    genere = "Dramma Storico",
                    eta = 12,
                    valutazioneMedia = valutazione_iniziale,
                    imageUrl = serieTv3.imageUrl, // Usa l'immagine della serie TV
                    durataSecondi = 3400L,
                    idSerieDiAppartenenza = serieTv3.id
                ),
                Episodio(
                    id = UUID.randomUUID().toString(),
                    trama = "La Regina Mary si adatta alla vita dopo l'abdicazione del Re Edoardo VIII.",
                    nome = "Windsor",
                    genere = "Dramma Storico",
                    eta = 12,
                    valutazioneMedia = valutazione_iniziale,
                    imageUrl = serieTv3.imageUrl,
                    durataSecondi = 3500L,
                    idSerieDiAppartenenza = serieTv3.id
                )
            )
            episodiSerie3.forEach { episodioDao.insertEpisodio(it) }
            Log.d(TAG, "Episodi per ${serieTv3.nome} inseriti.")

            // --- Nuove Serie TV Aggiunte ---

            // Serie TV 4: Breaking Bad
            val serieTv4 = SerieTv(
                id = UUID.randomUUID().toString(),
                trama = "Un professore di chimica del liceo con il cancro terminale si rivolge alla produzione di metanfetamine per assicurare il futuro finanziario della sua famiglia.",
                nome = "Breaking Bad",
                genere = "Crime, Thriller, Drammatico",
                eta = 16,
                valutazioneMedia = valutazione_iniziale,
                imageUrl = "breaking_bad_poster"
            )
            serieTvDao.insertSerieTv(serieTv4)
            Log.d(TAG, "Serie TV 4 inserita: ${serieTv4.nome}")

            val episodiSerie4 = listOf(
                Episodio(
                    id = UUID.randomUUID().toString(),
                    trama = "Walter White, un professore di chimica, scopre di avere il cancro ai polmoni e decide di entrare nel mondo della droga.",
                    nome = "Pilota",
                    genere = "Crime, Thriller, Drammatico",
                    eta = 16,
                    valutazioneMedia = valutazione_iniziale,
                    imageUrl = serieTv4.imageUrl,
                    durataSecondi = 3400L,
                    idSerieDiAppartenenza = serieTv4.id
                ),
                Episodio(
                    id = UUID.randomUUID().toString(),
                    trama = "Walt e Jesse cercano di smaltire un corpo e un sacco di metanfetamine.",
                    nome = "Il gatto è nel sacco...",
                    genere = "Crime, Thriller, Drammatico",
                    eta = 16,
                    valutazioneMedia = valutazione_iniziale,
                    imageUrl = serieTv4.imageUrl,
                    durataSecondi = 3000L,
                    idSerieDiAppartenenza = serieTv4.id
                ),
                Episodio(
                    id = UUID.randomUUID().toString(),
                    trama = "Walt usa le sue conoscenze chimiche per aiutare Jesse a vendere la metanfetamina.",
                    nome = "Ehi, piccolo!",
                    genere = "Crime, Thriller, Drammatico",
                    eta = 16,
                    valutazioneMedia = valutazione_iniziale,
                    imageUrl = serieTv4.imageUrl,
                    durataSecondi = 3100L,
                    idSerieDiAppartenenza = serieTv4.id
                )
            )
            episodiSerie4.forEach { episodioDao.insertEpisodio(it) }
            Log.d(TAG, "Episodi per ${serieTv4.nome} inseriti.")

            // Serie TV 5: Friends
            val serieTv5 = SerieTv(
                id = UUID.randomUUID().toString(),
                trama = "Le vite di sei amici che vivono a Manhattan mentre affrontano le sfide della vita adulta, del lavoro e delle relazioni.",
                nome = "Friends",
                genere = "Commedia, Romantico",
                eta = 10,
                valutazioneMedia = valutazione_iniziale,
                imageUrl = "friends_poster"
            )
            serieTvDao.insertSerieTv(serieTv5)
            Log.d(TAG, "Serie TV 5 inserita: ${serieTv5.nome}")

            val episodiSerie5 = listOf(
                Episodio(
                    id = UUID.randomUUID().toString(),
                    trama = "Rachel arriva al Central Perk dopo aver lasciato il suo quasi-marito all'altare.",
                    nome = "Pilota",
                    genere = "Commedia, Romantico",
                    eta = 10,
                    valutazioneMedia = valutazione_iniziale,
                    imageUrl = serieTv5.imageUrl,
                    durataSecondi = 1320L, // 22 minuti
                    idSerieDiAppartenenza = serieTv5.id
                ),
                Episodio(
                    id = UUID.randomUUID().toString(),
                    trama = "Monica cucina una cena per la sua nuova relazione, mentre Joey cerca di recuperare un reggiseno.",
                    nome = "L'episodio col segno sul sedere",
                    genere = "Commedia, Romantico",
                    eta = 10,
                    valutazioneMedia = valutazione_iniziale,
                    imageUrl = serieTv5.imageUrl,
                    durataSecondi = 1320L,
                    idSerieDiAppartenenza = serieTv5.id
                ),
                Episodio(
                    id = UUID.randomUUID().toString(),
                    trama = "Phoebe tenta di ottenere un contratto discografico mentre Ross cerca di aiutare Carol a decorare l'appartamento.",
                    nome = "L'episodio col pollo",
                    genere = "Commedia, Romantico",
                    eta = 10,
                    valutazioneMedia = valutazione_iniziale,
                    imageUrl = serieTv5.imageUrl,
                    durataSecondi = 1320L,
                    idSerieDiAppartenenza = serieTv5.id
                )
            )
            episodiSerie5.forEach { episodioDao.insertEpisodio(it) }
            Log.d(TAG, "Episodi per ${serieTv5.nome} inseriti.")

            // Serie TV 6: Game of Thrones
            val serieTv6 = SerieTv(
                id = UUID.randomUUID().toString(),
                trama = "Nove famiglie nobili lottano per il controllo delle terre mitiche di Westeros, mentre un'antica minaccia si risveglia a nord.",
                nome = "Il Trono di Spade",
                genere = "Fantasy, Drammatico, Avventura",
                eta = 18,
                valutazioneMedia = valutazione_iniziale,
                imageUrl = "game_of_thrones_poster"
            )
            serieTvDao.insertSerieTv(serieTv6)
            Log.d(TAG, "Serie TV 6 inserita: ${serieTv6.nome}")

            val episodiSerie6 = listOf(
                Episodio(
                    id = UUID.randomUUID().toString(),
                    trama = "Lord Eddard Stark riceve una visita dal Re Robert Baratheon e gli viene offerta una posizione di prestigio.",
                    nome = "L'inverno sta arrivando",
                    genere = "Fantasy, Drammatico, Avventura",
                    eta = 18,
                    valutazioneMedia = valutazione_iniziale,
                    imageUrl = serieTv6.imageUrl,
                    durataSecondi = 3600L, // 60 minuti
                    idSerieDiAppartenenza = serieTv6.id
                ),
                Episodio(
                    id = UUID.randomUUID().toString(),
                    trama = "Bran si riprende ma non ricorda nulla, mentre Daenerys si adatta alla sua nuova vita.",
                    nome = "La strada del Re",
                    genere = "Fantasy, Drammatico, Avventura",
                    eta = 18,
                    valutazioneMedia = valutazione_iniziale,
                    imageUrl = serieTv6.imageUrl,
                    durataSecondi = 3540L, // 59 minuti
                    idSerieDiAppartenenza = serieTv6.id
                ),
                Episodio(
                    id = UUID.randomUUID().toString(),
                    trama = "Eddard Stark indaga sulla morte del precedente Primo Cavaliere e scopre segreti pericolosi.",
                    nome = "Lord Snow",
                    genere = "Fantasy, Drammatico, Avventura",
                    eta = 18,
                    valutazioneMedia = valutazione_iniziale,
                    imageUrl = serieTv6.imageUrl,
                    durataSecondi = 3540L,
                    idSerieDiAppartenenza = serieTv6.id
                )
            )
            episodiSerie6.forEach { episodioDao.insertEpisodio(it) }
            Log.d(TAG, "Episodi per ${serieTv6.nome} inseriti.")

            Log.d(TAG, "Popolamento database completato (totale: 6 Serie TV, 18 Episodi)")
        }
    }

    suspend fun clearDatabase(context: Context) {
        withContext(Dispatchers.IO) {
            val db = AppDatabase.getDatabase(context)
            Log.d(TAG, "Inizio pulizia database...")
            db.clearAllTables() // Questo è il metodo Room per cancellare tutti i dati
            Log.d(TAG, "Database pulito.")
        }
    }

    fun startClearing(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            clearDatabase(context)
        }
    }
}