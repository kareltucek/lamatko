package lamatko

import lamatko.ProfileGenerator.impl.simplifyToAscii
import lamatko.ProfileGenerator.impl.sliceBy
import lamatko.ProfileGenerator.impl.toProbabilityProfile
import java.text.Normalizer
import java.util.*
import kotlin.text.Typography.degree

object ProfileGenerator {
    fun generateProfileFromString(input: String, degrees: List<Int> = listOf(3)): BackgroundProfile {
        val simplifiedInput = input.simplifyToAscii()

        return degrees
            .map { currentDegree ->
                simplifiedInput
                    .sliceBy(currentDegree)
                    .toProbabilityProfile(currentDegree)
            }
            .let { BackgroundProfile(it) }
    }

    object impl {
        fun String.removeDiacritics(): String {
            return Normalizer.normalize(this, Normalizer.Form.NFD)
                .replace("\\p{Mn}+".toRegex(), "")
        }

        fun String.simplifyToAscii(): String {
            return this
                .removeDiacritics()
                .lowercase(Locale.getDefault())
                .replace("[^a-z]".toRegex(), "")
        }

        fun String.sliceBy(n: Int): List<String> {
            return (0 .. (this.length - n))
                .map { this.substring(it, it+n) }
        }

        fun List<String>.toProbabilityProfile(degree: Int): BackgroundProfile.FrequencyMap {
            val frequencyMap = this
                .groupBy { it }
                .mapValues { entry -> entry.value.size }

            val sum = frequencyMap.values.sum()

            fun toLog(occurences: Double) = kotlin.math.ln(occurences / sum * frequencyMap.keys.size)

            val normalizedFrequencyMap = frequencyMap
                .mapValues { entry -> toLog(entry.value.toDouble()) }

            /**
             * TODO: consider normalizing rating by result length, as some alphabets may produce longer outputs than
             *       others, which may bias the rating.
             * */
            return BackgroundProfile.FrequencyMap(map = normalizedFrequencyMap, degree = degree, defaultRating = toLog(0.5))
        }
    }

    /**
     * Thanks goes to Project Gutenberg and Fyodor Dostoevsky.
     */
    object SeedTexts {
        val czech = """
            V dalekých krajích Sibiře, uprostřed stepí, hor a
            neproniknutelných lesů vyskytují se zřídka malinká města s jedním
            nebo nanejvýš se dvěma tisíci obyvatelů, dřevěná to, neúhledná
            města se dvěma chrámy, jedním ve městě, druhým na hřbitově, a
            podobná více k slušné vesnici pod Moskvou než k městu. Bývají
            obyčejně hojně opatřena policejními hejtmany, komisary a
            ostatními podřízenými policejními dozorci. V Sibiři vůbec přes
            to, že je tam zima. jsou úřady neobyčejně teploučké. Lid tamní je
            prostý, nenačichlý liberálností; pořádky staré, pevné, staletími
            posvěcené. Úředníky, kteří právem hrají úlohu sibiřské šlechty,
            jsou buď tuzemci, zakořenělí Sibiřáci, anebo rodáci z evropského
            Ruska, zejména hlavních měst, kteří se dali přivábiti přídavkem k
            služnému, dvojnásobnou náhradou cestovného a svůdnými nadějemi do
            budoucna. Ti z nich, kteří umějí řešiti hádanku života, zůstávají
            skoro všichni v Sibiři, rádi se v ní usazují a zapouštějí pevně
            kořeny. Za to později nesou bohaté a sladké ovoce. Jiné však,
            lidi to lehkomyslné, kteří neumějí řešiti hádanku života, Sibiř
            brzy omrzí a před nimi vzniká tesklivá otázka: Proč jen sem
            přijeli? Nemohou se dočkati, kdy vyprší zákonitá lhůta úředního
            pobytu v Sibiři, totiž tři léta, a jakmile uplynula, ihned se
            namáhají, aby byli převedeni na jiné místo, vracejí se do své
            otčiny, spílají Sibiři a tropí si z ní žerty. Avšak neprávem: v
            Sibiři může býti člověk blaženě živ nejen jako úředník, nýbrž i
            vzhledem k mnohým jiným okolnostem. Podnebí jest výtečné; je tam
            mnoho neobyčejně bohatých a pohostinných obchodníků; mnoho
            nevšedně zámožných jinorodců. Dívky kvetou růžemi a jsou
            svrchovaně mravny. Pernatá zvěř lítá po ulicích a sama přiletuje
            k lovci. Šampaňského vypije se tak mnoho, že neuvěříte. Kaviár je
            ku podivu. Úroda v některých místnostech přináší patnáct zrn z
            jednoho... Vůbec země požehnaná. Jest pouze zapotřebí, aby jí
            člověk uměl užiti. A v Sibiři jí užívati umějí.

            V jednom z takových veselých, sebou spokojených městeček s
            roztomilým obyvatelstvem, jehož památka nevymizí z mého srdce,
            setkal jsem se s Alexandrem Petrovičem Gorjančikovem, tamním
            osadníkem, rodilým šlechticem a statkářem z evropského Ruska,
            odkudž pro vraždu své ženy byl odeslán na Sibiř, kde konal nucené
            práce jako trestanec druhé třídy, a když uplynula zákonem mu
            vyměřená desetiletá lhůta trestu, stal se osadníkem ve městečku
            K., kdež pokorně a tiše trávil ostatek svého života. Domovské
            právo měl vlastně v jedné volosti*), sousedící s městem, ale
            bydlel ve městě, kde se mu naskytovala možnost, opatřiti si
            třebas jen skrovnou výživu vyučováním dětí. V sibiřských městech
            často se setkáte s učiteli, bývalými trestanci; jimi nepovrhují.
            Vyučují hlavně francouzskému jazyku, bez něhož se člověk neobejde
            v běhu života, o němž by však bez nich ve vzdálených krajích
            Sibiře neměli ani ponětí.

            Poprvé jsem se setkal s Alexandrem Petrovičem v domě jistého
            starého, zasloužilého a pohostinného úředníka Ivana Ivanoviče
            Gvozdikova, jenž měl pět velice nadějných dcer různého stáří.
            Alexandr Petrovič jim dával hodiny čtyřikrát za týden, po třiceti
            kopejkách stříbra za hodinu. Jeho zevnějšek obrátil k sobě mou
            pozornost. Byl to neobyčejně bledý, hubený člověk, ještě ne
            starý, asi třicetipětiletý, malinký a slaboučký. Oblečen býval
            vždycky velmi čistě po evropsku. Dali-li jste se s ním do řeči,
            hleděl na vás ne-

            *) Volostí slově venkovský, do jisté míry samosprávný okrsek, s
            voleným starostou a vlastním, voleným soudem, pod jehož pravomoc
            spadají však jen členové selských obcí.

            obyčejně upřeně a pozorně, s přísnou zdvořilostí vyslechl každé
            vaše slovo, jako by se snažil, vmysliti se v jeho smysl, jako
            byste mu svou otázkou byli dali hádanku, anebo se chcete
            dopátrati některého jeho tajemství; pak teprv odpovídal jasně a
            krátce, ale s takovým důrazem na každém slově své odpovědi, že se
            vás najednou — bůh ví proč -zmocnil nepříjemný pocit a konečně
            jste byli sami rádi, že je rozhovor skončen. Vyptal jsem se na
            něho hned tehdy Ivana Ivanoviče a dověděl jsem se, že Gorjančikov
            vede bezúhonný, mravný život, sice by ho Ivan Ivanovič nevzal za
            učitele ke svým dcerám; ale že se hrozně straní společnosti, před
            každým se schovává, je neobyčejně učený, mnoho čte, ale mluví
            velice málo a vůbec že je dosti obtížno dáti se s ním do řeči.
            Někteří tvrdili, že je jistě blázen, ačkoli připouštěli zároveň,
            že to ve skutečnosti není hrubě důležitá vada, že mnozí z
            poctivých sousedů městečka jsou hotovi prokazovati všemožnou
            laskavost Alexandru Petroviči, že by dokonce mohl být i
            užitečným, že by na příklad mohl spisovati prosebné listy.
            Domýšleli se, že musí míti v Rusku slušné příbuzenstvo, snad
            dokonce i lidi, mající značný vliv, ale věděli, že od té chvíle,
            co byl odsouzen k deportaci, přetrhl rozhodně všeliké s nimi
            spojení — slovem, že si sám škodí. Kromě toho všichni u nás znali
            jeho historii, věděli, že zabil svou ženu hned v prvním roce
            svého manželství, že ji zabil ze žárlivosti a sám se udal, což
            bylo značně polehčující okolností při vyměření trestu. Na podobné
            přestupky pohlíží se vždycky jako na neštěstí, hodné politování.
            Ale přes to přese všechno podivín se důsledně stranil všech a
            přicházel mezi lidi výhradně jen dávat hodiny.

            Z prva jsem si ho hrubě nevšímal, ale pomálu — nevím ani, proč —
            počal mne zajímati. Vězelo v něm cosi záhadného. Abychom se spolu
            rozhovořili, na to nebylo ani pomyšlení. Ovšem na mé otázky
            odpovídal vždycky, ba zdálo se při tom, jako by to pokládal za
            svou nejpřednější povinnost. Ale když jsem vyslechl jeho
            odpovědi, zdávalo se mi nevhodným dále ho vyslýchati. Kromě toho
            po každé takové rozprávce bylo viděti na jeho tváři jakési
            strádání a únavu. Vzpomínám si, jak jsem šel s ním kdysi v
            překrásný letní večer od Ivana Ivanovice. Náhle mne napadlo,
            abych ho pozval na chvilenku k sobě, že si vykouříme po
            papirosce. Nemohu vám vylíčiti, jaký úžas se vyjádřil na jeho
            tváři; jako by hlavu ztratil, počal mumlati jakási nesouvislá
            slova, pojednou vzhlédl na mne pln hněvu a dal se na útek v
            opačnou stranu. Já byl celý udiven.
        """.trimIndent()

        val english = """
            In the midst of the steppes, of the mountains, of the impenetrable
            forests of the desert regions of Siberia, one meets from time to time
            with little towns of a thousand or two inhabitants, built entirely of
            wood, very ugly, with two churches--one in the centre of the town, the
            other in the cemetery--in a word, towns which bear much more resemblance
            to a good-sized village in the suburbs of Moscow than to a town properly
            so called. In most cases they are abundantly provided with
            police-master, assessors, and other inferior officials. If it is cold in
            Siberia, the great advantages of the Government service compensate for
            it. The inhabitants are simple people, without liberal ideas. Their
            manners are antique, solid, and unchanged by time. The officials who
            form, and with reason, the nobility in Siberia, either belong to the
            country, deeply-rooted Siberians, or they have arrived there from
            Russia. The latter come straight from the capitals, tempted by the high
            pay, the extra allowance for travelling expenses, and by hopes not less
            seductive for the future. Those who know how to resolve the problem of
            life remain almost always in Siberia; the abundant and richly-flavoured
            fruit which they gather there recompenses them amply for what they lose.

            As for the others, light-minded persons who are unable to deal with the
            problem, they are soon bored in Siberia, and ask themselves with regret
            why they committed the folly of coming. They impatiently kill the three
            years which they are obliged by rule to remain, and as soon as their
            time is up, they beg to be sent back, and return to their original
            quarters, running down Siberia, and ridiculing it. They are wrong, for
            it is a happy country, not only as regards the Government service, but
            also from many other points of view.

            The climate is excellent, the merchants are rich and hospitable, the
            Europeans in easy circumstances are numerous; as for the young girls,
            they are like roses and their morality is irreproachable. Game is to be
            found in the streets, and throws itself upon the sportsman's gun. People
            drink champagne in prodigious quantities. The caviare is astonishingly
            good and most abundant. In a word, it is a blessed land, out of which it
            is only necessary to be able to make profit; and much profit is really
            made.

            It is in one of these little towns--gay and perfectly satisfied with
            themselves, the population of which has left upon me the most agreeable
            impression--that I met an exile, Alexander Petrovitch Goriantchikoff,
            formerly a landed proprietor in Russia. He had been condemned to hard
            labour of the second class for assassinating his wife. After undergoing
            his punishment--ten years of hard labour--he lived quietly and unnoticed
            as a colonist in the little town of K----. To tell the truth, he was
            inscribed in one of the surrounding districts; but he resided at K----,
            where he managed to get a living by giving lessons to children. In the
            towns of Siberia one often meets with exiles who are occupied with
            instruction. They are not looked down upon, for they teach the French
            language, so necessary in life, and of which without them one would not,
            in the distant parts of Siberia, have the least idea.

            I saw Alexander Petrovitch the first time at the house of an official,
            Ivan Ivanitch Gvosdikof, a venerable old man, very hospitable, and the
            father of five daughters, of whom the greatest hopes were entertained.
            Four times a week Alexander Petrovitch gave them lessons, at the rate of
            thirty kopecks silver a lesson. His external appearance interested me.
            He was excessively pale and thin, still young--about thirty-five years
            of age--short and weak, always very neatly dressed in the European
            style. When you spoke to him he looked at you in a very attentive
            manner, listening to your words with strict politeness, and with a
            reflective air, as though you had placed before him a problem or wished
            to extract from him a secret. He replied clearly and shortly; but in
            doing so, weighed each word, so that one felt ill at ease without
            knowing why, and was glad when the conversation came to an end. I put
            some questions to Ivan Gvosdikof in regard to him. He told me that
            Goriantchikoff was of irreproachable morals, otherwise Gvosdikof would
            not have entrusted him with the education of his children; but that he
            was a terrible misanthrope, who kept apart from all society; that he was
            very learned, a great reader, and that he spoke but little, and never
            entered freely into a conversation. Certain persons told him that he was
            mad; but that was not looked upon as a very serious defect. Accordingly,
            the most important persons in the town were ready to treat Alexander
            Petrovitch with respect, for he could be useful to them in writing
            petitions. It was believed that he was well connected in Russia.
            Perhaps, among his relations, there were some who were highly placed;
            but it was known that since his exile he had broken off all relations
            with them. In a word--he injured himself. Every one knew his story, and
            was aware that he had killed his wife, through jealousy, less than a
            year after his marriage; and that he had given himself up to justice;
            which had made his punishment much less severe. Such crimes are always
            looked upon as misfortunes, which must be treated with pity.
            Nevertheless, this original kept himself obstinately apart, and never
            showed himself except to give lessons. In the first instance I paid no
            attention to him; then, without knowing why, I found myself interested
            by him. He was rather enigmatic; to talk with him was quite impossible.
            Certainly he replied to all my questions; he seemed to make it a duty to
            do so; but when once he had answered, I was afraid to interrogate him
            any longer.

            After such conversations one could observe on his countenance signs of
            suffering and exhaustion. I remember that, one fine summer evening, I
            went out with him from the house of Ivan Gvosdikof. It suddenly occurred
            to me to invite him to come in with me and smoke a cigarette. I can
            scarcely describe the fright which showed itself in his countenance. He
            became confused, muttered incoherent words, and suddenly, after looking
            at me with an angry air, took to flight in an opposite direction. I was
            very much astonished afterwards, when he met me. He seemed to
            experience, on seeing me, a sort of terror; but I did not lose courage.
            There was something in him which attracted me.

            A month afterwards I went to see Petrovitch without any pretext. It is
            evident that, in doing so, I behaved foolishly, and without the least
            delicacy. He lived at one of the extreme points of the town with an old
            woman whose daughter was in a consumption. The latter had a little child
            about ten years old, very pretty and very lively.

            When I went in Alexander Petrovitch was seated by her side, and was
            teaching her to read. When he saw me he became confused, as if I had
            detected him in a crime. Losing all self-command, he suddenly stood up
            and looked at me with awe and astonishment. Then we both of us sat down.
            He followed attentively all my looks, as if I had suspected him of some
            mysterious intention. I understood he was horribly mistrustful. He
            looked at me as a sort of spy, and he seemed to be on the point of
            saying, "Are you not soon going away?"
        """.trimIndent()
    }
}