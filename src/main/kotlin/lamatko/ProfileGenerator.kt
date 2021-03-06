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
            V dalek??ch kraj??ch Sibi??e, uprost??ed step??, hor a
            neproniknuteln??ch les?? vyskytuj?? se z????dka malink?? m??sta s jedn??m
            nebo nanejv???? se dv??ma tis??ci obyvatel??, d??ev??n?? to, ne??hledn??
            m??sta se dv??ma chr??my, jedn??m ve m??st??, druh??m na h??bitov??, a
            podobn?? v??ce k slu??n?? vesnici pod Moskvou ne?? k m??stu. B??vaj??
            oby??ejn?? hojn?? opat??ena policejn??mi hejtmany, komisary a
            ostatn??mi pod????zen??mi policejn??mi dozorci. V Sibi??i v??bec p??es
            to, ??e je tam zima. jsou ????ady neoby??ejn?? teplou??k??. Lid tamn?? je
            prost??, nena??ichl?? liber??lnost??; po????dky star??, pevn??, stalet??mi
            posv??cen??. ????edn??ky, kte???? pr??vem hraj?? ??lohu sibi??sk?? ??lechty,
            jsou bu?? tuzemci, zako??en??l?? Sibi????ci, anebo rod??ci z evropsk??ho
            Ruska, zejm??na hlavn??ch m??st, kte???? se dali p??iv??biti p????davkem k
            slu??n??mu, dvojn??sobnou n??hradou cestovn??ho a sv??dn??mi nad??jemi do
            budoucna. Ti z nich, kte???? um??j?? ??e??iti h??danku ??ivota, z??st??vaj??
            skoro v??ichni v Sibi??i, r??di se v n?? usazuj?? a zapou??t??j?? pevn??
            ko??eny. Za to pozd??ji nesou bohat?? a sladk?? ovoce. Jin?? v??ak,
            lidi to lehkomysln??, kte???? neum??j?? ??e??iti h??danku ??ivota, Sibi??
            brzy omrz?? a p??ed nimi vznik?? teskliv?? ot??zka: Pro?? jen sem
            p??ijeli? Nemohou se do??kati, kdy vypr???? z??konit?? lh??ta ????edn??ho
            pobytu v Sibi??i, toti?? t??i l??ta, a jakmile uplynula, ihned se
            nam??haj??, aby byli p??evedeni na jin?? m??sto, vracej?? se do sv??
            ot??iny, sp??laj?? Sibi??i a trop?? si z n?? ??erty. Av??ak nepr??vem: v
            Sibi??i m????e b??ti ??lov??k bla??en?? ??iv nejen jako ????edn??k, n??br?? i
            vzhledem k mnoh??m jin??m okolnostem. Podneb?? jest v??te??n??; je tam
            mnoho neoby??ejn?? bohat??ch a pohostinn??ch obchodn??k??; mnoho
            nev??edn?? z??mo??n??ch jinorodc??. D??vky kvetou r????emi a jsou
            svrchovan?? mravny. Pernat?? zv???? l??t?? po ulic??ch a sama p??iletuje
            k lovci. ??ampa??sk??ho vypije se tak mnoho, ??e neuv??????te. Kavi??r je
            ku podivu. ??roda v n??kter??ch m??stnostech p??in?????? patn??ct zrn z
            jednoho... V??bec zem?? po??ehnan??. Jest pouze zapot??eb??, aby j??
            ??lov??k um??l u??iti. A v Sibi??i j?? u????vati um??j??.

            V jednom z takov??ch vesel??ch, sebou spokojen??ch m??ste??ek s
            roztomil??m obyvatelstvem, jeho?? pam??tka nevymiz?? z m??ho srdce,
            setkal jsem se s Alexandrem Petrovi??em Gorjan??ikovem, tamn??m
            osadn??kem, rodil??m ??lechticem a statk????em z evropsk??ho Ruska,
            odkud?? pro vra??du sv?? ??eny byl odesl??n na Sibi??, kde konal nucen??
            pr??ce jako trestanec druh?? t????dy, a kdy?? uplynula z??konem mu
            vym????en?? desetilet?? lh??ta trestu, stal se osadn??kem ve m??ste??ku
            K., kde?? pokorn?? a ti??e tr??vil ostatek sv??ho ??ivota. Domovsk??
            pr??vo m??l vlastn?? v jedn?? volosti*), soused??c?? s m??stem, ale
            bydlel ve m??st??, kde se mu naskytovala mo??nost, opat??iti si
            t??ebas jen skrovnou v????ivu vyu??ov??n??m d??t??. V sibi??sk??ch m??stech
            ??asto se setk??te s u??iteli, b??val??mi trestanci; jimi nepovrhuj??.
            Vyu??uj?? hlavn?? francouzsk??mu jazyku, bez n??ho?? se ??lov??k neobejde
            v b??hu ??ivota, o n??m?? by v??ak bez nich ve vzd??len??ch kraj??ch
            Sibi??e nem??li ani pon??t??.

            Poprv?? jsem se setkal s Alexandrem Petrovi??em v dom?? jist??ho
            star??ho, zaslou??il??ho a pohostinn??ho ????edn??ka Ivana Ivanovi??e
            Gvozdikova, jen?? m??l p??t velice nad??jn??ch dcer r??zn??ho st??????.
            Alexandr Petrovi?? jim d??val hodiny ??ty??ikr??t za t??den, po t??iceti
            kopejk??ch st????bra za hodinu. Jeho zevn??j??ek obr??til k sob?? mou
            pozornost. Byl to neoby??ejn?? bled??, huben?? ??lov??k, je??t?? ne
            star??, asi t??icetip??tilet??, malink?? a slabou??k??. Oble??en b??val
            v??dycky velmi ??ist?? po evropsku. Dali-li jste se s n??m do ??e??i,
            hled??l na v??s ne-

            *) Volost?? slov?? venkovsk??, do jist?? m??ry samospr??vn?? okrsek, s
            volen??m starostou a vlastn??m, volen??m soudem, pod jeho?? pravomoc
            spadaj?? v??ak jen ??lenov?? selsk??ch obc??.

            oby??ejn?? up??en?? a pozorn??, s p????snou zdvo??ilost?? vyslechl ka??d??
            va??e slovo, jako by se sna??il, vmysliti se v jeho smysl, jako
            byste mu svou ot??zkou byli dali h??danku, anebo se chcete
            dop??trati n??kter??ho jeho tajemstv??; pak teprv odpov??dal jasn?? a
            kr??tce, ale s takov??m d??razem na ka??d??m slov?? sv?? odpov??di, ??e se
            v??s najednou ??? b??h v?? pro?? -zmocnil nep????jemn?? pocit a kone??n??
            jste byli sami r??di, ??e je rozhovor skon??en. Vyptal jsem se na
            n??ho hned tehdy Ivana Ivanovi??e a dov??d??l jsem se, ??e Gorjan??ikov
            vede bez??honn??, mravn?? ??ivot, sice by ho Ivan Ivanovi?? nevzal za
            u??itele ke sv??m dcer??m; ale ??e se hrozn?? stran?? spole??nosti, p??ed
            ka??d??m se schov??v??, je neoby??ejn?? u??en??, mnoho ??te, ale mluv??
            velice m??lo a v??bec ??e je dosti obt????no d??ti se s n??m do ??e??i.
            N??kte???? tvrdili, ??e je jist?? bl??zen, a??koli p??ipou??t??li z??rove??,
            ??e to ve skute??nosti nen?? hrub?? d??le??it?? vada, ??e mnoz?? z
            poctiv??ch soused?? m??ste??ka jsou hotovi prokazovati v??emo??nou
            laskavost Alexandru Petrovi??i, ??e by dokonce mohl b??t i
            u??ite??n??m, ??e by na p????klad mohl spisovati prosebn?? listy.
            Dom????leli se, ??e mus?? m??ti v Rusku slu??n?? p????buzenstvo, snad
            dokonce i lidi, maj??c?? zna??n?? vliv, ale v??d??li, ??e od t?? chv??le,
            co byl odsouzen k deportaci, p??etrhl rozhodn?? v??elik?? s nimi
            spojen?? ??? slovem, ??e si s??m ??kod??. Krom?? toho v??ichni u n??s znali
            jeho historii, v??d??li, ??e zabil svou ??enu hned v prvn??m roce
            sv??ho man??elstv??, ??e ji zabil ze ????rlivosti a s??m se udal, co??
            bylo zna??n?? poleh??uj??c?? okolnost?? p??i vym????en?? trestu. Na podobn??
            p??estupky pohl?????? se v??dycky jako na ne??t??st??, hodn?? politov??n??.
            Ale p??es to p??ese v??echno podiv??n se d??sledn?? stranil v??ech a
            p??ich??zel mezi lidi v??hradn?? jen d??vat hodiny.

            Z prva jsem si ho hrub?? nev????mal, ale pom??lu ??? nev??m ani, pro?? ???
            po??al mne zaj??mati. V??zelo v n??m cosi z??hadn??ho. Abychom se spolu
            rozhovo??ili, na to nebylo ani pomy??len??. Ov??em na m?? ot??zky
            odpov??dal v??dycky, ba zd??lo se p??i tom, jako by to pokl??dal za
            svou nejp??edn??j???? povinnost. Ale kdy?? jsem vyslechl jeho
            odpov??di, zd??valo se mi nevhodn??m d??le ho vysl??chati. Krom?? toho
            po ka??d?? takov?? rozpr??vce bylo vid??ti na jeho tv????i jak??si
            str??d??n?? a ??navu. Vzpom??n??m si, jak jsem ??el s n??m kdysi v
            p??ekr??sn?? letn?? ve??er od Ivana Ivanovice. N??hle mne napadlo,
            abych ho pozval na chvilenku k sob??, ??e si vykou????me po
            papirosce. Nemohu v??m vyl????iti, jak?? ????as se vyj??d??il na jeho
            tv????i; jako by hlavu ztratil, po??al mumlati jak??si nesouvisl??
            slova, pojednou vzhl??dl na mne pln hn??vu a dal se na ??tek v
            opa??nou stranu. J?? byl cel?? udiven.
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