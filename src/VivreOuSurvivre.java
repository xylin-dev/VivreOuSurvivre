import extensions.File;
import extensions.CSVFile;

class VivreOuSurvivre extends Program {
    /* ============================== */
    /* VARIABLE ET CONSTANTE GLOBALES */
    /* ============================== */

    //Elément de la map
    final String CHEMIN = "⬛";
    final String ARBRE = "🌴";
    final String MONTAGNE = "🗻";
    final String BOMBE = "💣";
    final String LAVE = "🌋";
    final String CARTE = "🎴";

    //Chemin vers data.csv
    final String csvData = "ressources/CSVFile/data.csv";

    //Couleurs du texte selon de leurs fonctions
    final String VERT = "\u001B[32m"; //Réussite, objectif atteint.
    final String ROUGE = "\u001B[31m"; //Échec, erreur.
    final String BLEU = "\u001B[34m"; //Important, à ne pas négliger.
    final String JAUNE = "\u001B[33m"; //Alerte, attention nécessaire.
    String RESET = "\u001B[0m"; //Couleurs et Styles par défauts

    //Style du texte selon leurs fonctions
    final String GRAS = "\033[1m"; 

    //nombre de Vie du Joueur ainsi que son nombre de Vie Precedent pour Kaomiji
    int nbVie = 10;
    int nbViePrecedent = 10;

    //Coordonné pour effet des éléments de la map
    int[] idxBombe = new int[]{-1,-1,-1,-1};
    int[] idxArbre = new int[]{-1,-1,-1,-1};
    int[] idxLave = new int[]{-1,-1,-1,-1};
    int[] idxCarte = new int[]{-1,-1,-1,-1};


    void algorithm(){
        Joueur ludophile = newJoueur();
        Objectif but = newObjectif();
        String[][] map = new String[20][20];
        String proceed = "";
        begin(ludophile, but);
        while(!equals(proceed, "FINI") && nbVie>0){
            initialisationMap(map, ludophile, but);
            afficherMap(map, ludophile);
            while(objectifPasAtteint(ludophile, map, but) && nbVie>0){
                executionAlgorithme(ludophile, map, but);
            }
            proceed = poursuivreJeu(ludophile);
        }
    }

    /* ================ */
    /* Création de Type */
    /* ================ */

    //Création de type : Joueur
    Joueur newJoueur(){
        Joueur ludophile = new Joueur();
        ludophile.nbBouclier = 0;
        ludophile.immunite = false;
        ludophile.tutoriel = true;
        return ludophile;
    }

    //Création de type : Objectif
    Objectif newObjectif(){
        Objectif but = new Objectif();
        return but;
    }

    //Création de type : Selection
    Selection newSelection(){
        Selection choix = new Selection();
        choix.nbCases = new int[20];
        choix.nbChoix = new int[20];
        choix.direction = new int[20];
        choix.conditions = new String[20];
        return choix;
    }

    /* ======================= */
    /* Kaomiji : Maître du Jeu */
    /* ======================= */
    
    //Trouve le minimum entre deux nombre
    int min(int premierNb, int deuxiemeNb){
        if(premierNb>deuxiemeNb){
            return deuxiemeNb;
        }

        return premierNb;
    }

    void testMin(){
        assertEquals(1, min(2, 1));
        assertEquals(3, min(3, 5));
    }

    //Maitre du jeu : Kaomiji
    String maitreKaomiji(int nbChances){
        String[] kaomiji = new String[]{"(˶•ᴗ•˶)", "(˶˃ ᵕ ˂˶)", "O_o", "(⌐■-■)", "(ಠ_ಠ)>⌐■-■", "ಠ_ʖಠ", "ರ_ರ", "(ꐦ¬_¬)", "(⪖ ⩋⪕)", "୧(๑•̀ᗝ•́)૭", "(⌐■_■)︻デ═一"};
        int idx = min(length(kaomiji)-1, length(kaomiji) - (nbChances+1));

        if(nbChances<nbViePrecedent){
            idx = min(length(kaomiji)-1, length(kaomiji) - (nbChances+1));
            nbViePrecedent = nbChances;
        }

        return kaomiji[idx];
    }

    void testMaitreKaomiji(){
        int nbLife;

        nbLife = 10;
        assertEquals("(˶•ᴗ•˶)", maitreKaomiji(nbLife));

        nbLife = 9;
        assertEquals("(˶˃ ᵕ ˂˶)", maitreKaomiji(nbLife));

        nbLife = 8;
        assertEquals("O_o", maitreKaomiji(nbLife));
    }

    //e dans le texte (principalement pour Kaomiji)
    String espacement(String mot){
        String espace = "";

        for(int cpt=0; cpt<length(mot); cpt++){
            espace = espace + " ";
        }

        return espace;
    }

    void testEspacement(){
        assertEquals("  ", espacement("aa"));
        assertEquals("     ", espacement("bbbbb"));
    }

    //Facilitera les moments où Kaomiji parle (au lieu d'utiliser print() ou println())
    String kaomijiPhrase(String mot){
        return maitreKaomiji(nbVie) + " - " + mot;
    }

    void testKaomijiOrateur(){
        assertEquals("(˶•ᴗ•˶) - Salut", kaomijiPhrase("Salut"));
    }

    //Affichera les paroles de Kaomiji
    void kaomijiOrateur(String mot){
        print(kaomijiPhrase(mot));
    }

    //Affichera les paroles de Kaomiji avec un saut à la ligne
    void kaomijiOrateurln(String mot){
        println(kaomijiPhrase(mot));
        delay(1000);
    }

    /* =============================================== */
    /* Création, initialisation et affichage de la map */
    /* =============================================== */

    //Remplisaage de la map avec des CHEMIN
    void remplissageMap(String[][] map){
        for(int idxI=0; idxI<length(map,1); idxI++){
            for(int idxJ=0; idxJ<length(map,2); idxJ++){
                map[idxI][idxJ] = CHEMIN;
            }
        }
    }

    //Ajout aléatoire des élements dans la carte selon tab
    void elementMap(String[][] map, String[] tab, double probabilite, int idxL, int idxC){
        double probabiliteAleatoire = random();
        int idxAleatoire = (int) (random()*length(tab));
        if(probabiliteAleatoire<probabilite){
            map[idxL][idxC] = tab[idxAleatoire];
        }
    }

    //Placement aléatoire de l'objectif du joueur dans la moitié de la map superieur
    void objectifMap(String[][] map, Objectif but){
        but.idxL = (int)(random()*(length(map, 1)-1));
        but.idxC = (int)(random()*(length(map, 2)-1));
        map[but.idxL][but.idxC] = but.DRAPEAU;
        map[but.idxL+1][but.idxC] = CHEMIN; //Pour éviter un montagne au cas où
        map[but.idxL][but.idxC+1] = CHEMIN;
    }

    //Placement du joueur dans la map
    void placementJoueur(String[][] map, Joueur ludophile){
        ludophile.idxL = length(map, 1)-1;
        ludophile.idxC = length(map, 2)-1;
        map[ludophile.idxL][ludophile.idxC] = ludophile.personnage;
        map[ludophile.idxL-1][ludophile.idxC] = CHEMIN; //Pour éviter un montagne au cas où
        map[ludophile.idxL][ludophile.idxC-1] = CHEMIN; 
    }

    //Reinitialisera le coordonnées des éléments de la map
    void idxElement(){
        idxBombe = new int[]{-1,-1,-1,-1};
        idxArbre = new int[]{-1,-1,-1,-1};
        idxLave = new int[]{-1,-1,-1,-1};
        idxCarte = new int[]{-1,-1,-1,-1};
    }
    
    //Initialisera la carte
    void initialisationMap(String[][] map, Joueur ludophile, Objectif but){
        String[] element = new String[]{ARBRE, MONTAGNE, BOMBE, LAVE, CARTE};
        double probabilite;

        //Pour augmenter la difficulté et forcer le joueur à faire des sacrifices 😈    
        if(ludophile.nbReussite > 4 && ludophile.nbReussite <= 7){
            probabilite = 0.4;
        } else if(ludophile.nbReussite>7){
            probabilite = 0.6;
        } else {
            probabilite = 0.25;
        }

        remplissageMap(map);
        for(int idxL=0; idxL<length(map, 1); idxL++){
            for(int idxC=0; idxC<length(map,2); idxC++){
                elementMap(map, element, probabilite, idxL, idxC);
            }
        }

        idxElement();
        objectifMap(map, but);
        placementJoueur(map, ludophile);
    }

    //Affichera la carte
    void afficherMap(String[][] map, Joueur ludophile){
        println();
        for(int idxI=0; idxI<length(map,1); idxI++){
            for(int idxJ=0; idxJ<length(map,2); idxJ++){
                print(map[idxI][idxJ]);  
            }
            elementExplication(ludophile, idxI);
            println();
        } 
        informationJoueur(ludophile, map);
    }

    //Affichera la description de chaque effet des éléments + info
    void elementExplication(Joueur ludophile, int idxL){
        final String rules = "ressources/File/rules.txt";
        String line;
        File explication = newFile(rules);
        int idx = -1;

        while(idx<idxL && ready(explication) && ludophile.tutoriel == false){
            line = readLine(explication);
            idx++;
            if(idx == idxL){
                print(espacement("□□□□□") + line);
            }
        }
    }

    /* =============================================================================== */
    /* Vérification du saisie du joueur afin de s'assurer qu'il n'y ai pas d'exception */
    /* =============================================================================== */

    //Vérification que la saisie d'un string est un chiffre
    boolean estChiffre(String saisie){
        int idx = 0;
        while(idx<length(saisie)){
            if(charAt(saisie, idx)<'0' || charAt(saisie, idx)>'9'){
                return false;
            }
            idx++;
        }

        return length(saisie)>0;
    }

    void testEstChiffre(){
        assertTrue(estChiffre("500"));
        assertTrue(estChiffre("1"));
        assertFalse(estChiffre("1a"));
        assertFalse(estChiffre("aa"));
    }

    //Convertir la saisie String en Int
    int stringtoInt(String saisie){
        int toInt = 0;

        for(int idx=0; idx<length(saisie); idx++){
            toInt = toInt * 10 + (charAt(saisie, idx)-'0');
        }

        return toInt;
    }

    void testStringToInt(){
        assertEquals(0, stringtoInt("0"));
        assertEquals(1, stringtoInt("1"));
        assertEquals(3, stringtoInt("3"));
        assertEquals(15, stringtoInt("15"));
        assertEquals(100, stringtoInt("100"));
    }

    //Tant que la saisie n'est pas correct, le joueur devra saisir à nouveau
    int verificationString(String saisie){
        while(!estChiffre(saisie)){
            kaomijiOrateur(ROUGE + "Ton choix n'est pas bon, essaie encore : " + RESET);
            saisie = readString();
        }
        
        return stringtoInt(saisie);
    }

    void testVerificationString(){
        assertEquals(0, verificationString("0"));
        assertEquals(1, verificationString("1"));
        assertEquals(3, verificationString("3"));
        assertEquals(15, verificationString("15"));
        assertEquals(100, verificationString("100"));
    }

    boolean stringToBoolean(String booleen){
        return equals(booleen, "true");
    }

    void testStringToBoolean(){
        assertTrue(stringToBoolean("true"));
        assertFalse(stringToBoolean("false"));
        assertFalse(stringToBoolean("chocolat"));
    }

    /* =============================== */
    /* Déplacement et boucle du Joueur */
    /* =============================== */

    //Vérification du déplacement vers le Nord
    boolean deplacementImpossibleNord(Joueur ludophile, String[][] map){
        return ludophile.idxL == 0 || equals(map[ludophile.idxL-1][ludophile.idxC], MONTAGNE);
    }

    void testDeplacementImpossibleNord(){
        Joueur ludophile = newJoueur();
        ludophile.personnage = "👨";

        ludophile.idxL = 0;
        ludophile.idxC = 1;
        String[][] map = new String[][]{{CHEMIN,ludophile.personnage,CHEMIN},
                                        {CHEMIN,CHEMIN,CHEMIN},
                                        {CHEMIN,CHEMIN,CHEMIN}};
        assertTrue(deplacementImpossibleNord(ludophile, map));

        ludophile.idxL = 1;
        ludophile.idxC = 1;
        map = new String[][]{{CHEMIN,MONTAGNE,CHEMIN},
                             {CHEMIN,ludophile.personnage,CHEMIN},
                             {CHEMIN,CHEMIN,CHEMIN}};
        assertTrue(deplacementImpossibleNord(ludophile, map));
        
        ludophile.idxL = 1;
        ludophile.idxC = 1;
        map = new String[][]{{CHEMIN,CHEMIN,CHEMIN},
                             {CHEMIN,ludophile.personnage,CHEMIN},
                             {CHEMIN,CHEMIN,CHEMIN}};
        assertFalse(deplacementImpossibleNord(ludophile, map));
    }

    //Vérification du déplacement vers le Sud
    boolean deplacementImpossibleSud(Joueur ludophile, String[][] map){
        return ludophile.idxL == (length(map, 1)-1) || equals(map[ludophile.idxL+1][ludophile.idxC], MONTAGNE);
    }

    void testDeplacementImpossibleSud(){
        Joueur ludophile = newJoueur();
        ludophile.personnage = "👨";

        ludophile.idxL = 2;
        ludophile.idxC = 1;
        String[][] map = new String[][]{{CHEMIN,CHEMIN,CHEMIN},
                                        {CHEMIN,CHEMIN,CHEMIN},
                                        {CHEMIN,ludophile.personnage,CHEMIN}};
        assertTrue(deplacementImpossibleSud(ludophile, map));

        ludophile.idxL = 1;
        ludophile.idxC = 1;
        map = new String[][]{{CHEMIN,CHEMIN,CHEMIN},
                             {CHEMIN,ludophile.personnage,CHEMIN},
                             {CHEMIN,MONTAGNE,CHEMIN}};
        assertTrue(deplacementImpossibleSud(ludophile, map));
        
        ludophile.idxL = 1;
        ludophile.idxC = 1;
        map = new String[][]{{CHEMIN,CHEMIN,CHEMIN},
                             {CHEMIN,ludophile.personnage,CHEMIN},
                             {CHEMIN,CHEMIN,CHEMIN}};
        assertFalse(deplacementImpossibleSud(ludophile, map));
    }

    //Vérification du déplacement vers l'Ouest
    boolean deplacementImpossibleOuest(Joueur ludophile, String[][] map){
        return ludophile.idxC == 0 || equals(map[ludophile.idxL][ludophile.idxC-1], MONTAGNE);
    }

    void testDeplacementImpossibleOuest(){
        Joueur ludophile = newJoueur();
        ludophile.personnage = "👨";

        ludophile.idxL = 1;
        ludophile.idxC = 0;
        String[][] map = new String[][]{{CHEMIN,CHEMIN,CHEMIN},
                                        {ludophile.personnage,CHEMIN,CHEMIN},
                                        {CHEMIN,CHEMIN,CHEMIN}};
        assertTrue(deplacementImpossibleOuest(ludophile, map));

        ludophile.idxL = 1;
        ludophile.idxC = 1;
        map = new String[][]{{CHEMIN,CHEMIN,CHEMIN},
                             {MONTAGNE,ludophile.personnage,CHEMIN},
                             {CHEMIN,CHEMIN,CHEMIN}};
        assertTrue(deplacementImpossibleOuest(ludophile, map));
        
        ludophile.idxL = 1;
        ludophile.idxC = 1;
        map = new String[][]{{CHEMIN,CHEMIN,CHEMIN},
                             {CHEMIN,ludophile.personnage,CHEMIN},
                             {CHEMIN,CHEMIN,CHEMIN}};
        assertFalse(deplacementImpossibleOuest(ludophile, map));
    }

    //Vérification du déplacement vers l'Est
    boolean deplacementImpossibleEst(Joueur ludophile, String[][] map){
        return ludophile.idxC == (length(map, 2)-1) || equals(map[ludophile.idxL][ludophile.idxC+1], MONTAGNE);
    }

    void testDeplacementImpossibleEst(){
        Joueur ludophile = newJoueur();
        ludophile.personnage = "👨";

        ludophile.idxL = 1;
        ludophile.idxC = 2;
        String[][] map = new String[][]{{CHEMIN,CHEMIN,CHEMIN},
                                        {CHEMIN,CHEMIN,ludophile.personnage},
                                        {CHEMIN,CHEMIN,CHEMIN}};
        assertTrue(deplacementImpossibleEst(ludophile, map));

        ludophile.idxL = 1;
        ludophile.idxC = 1;
        map = new String[][]{{CHEMIN,CHEMIN,CHEMIN},
                             {CHEMIN,ludophile.personnage,MONTAGNE},
                             {CHEMIN,CHEMIN,CHEMIN}};
        assertTrue(deplacementImpossibleEst(ludophile, map));
        
        ludophile.idxL = 1;
        ludophile.idxC = 1;
        map = new String[][]{{CHEMIN,CHEMIN,CHEMIN},
                             {CHEMIN,ludophile.personnage,CHEMIN},
                             {CHEMIN,CHEMIN,CHEMIN}};
        assertFalse(deplacementImpossibleEst(ludophile, map));
    }

    //Avancer vers le Nord
    void avancerNord(Joueur ludophile, String[][] map, Objectif but){
        if(deplacementImpossibleNord(ludophile, map)){
            kaomijiOrateurln(ROUGE + "Ce déplacement n'est pas possible !" + RESET);
            erreurAlgorithme(ludophile, map, but);
        } else {
            map[ludophile.idxL-1][ludophile.idxC] = ludophile.personnage;
            ludophile.idxL--;
            map[ludophile.idxL+1][ludophile.idxC] = CHEMIN;
            afficherMap(map, ludophile);
            effetElement(ludophile, map, but);
        } 
    }

    //Avancer vers le Sud
    void avancerSud(Joueur ludophile, String[][] map, Objectif but){
        if(deplacementImpossibleSud(ludophile, map)){
            kaomijiOrateurln(ROUGE + "Ce déplacement n'est pas possible !" + RESET);
            erreurAlgorithme(ludophile, map, but);
        } else {
            map[ludophile.idxL+1][ludophile.idxC] = ludophile.personnage;
            ludophile.idxL++;
            map[ludophile.idxL-1][ludophile.idxC] = CHEMIN;
            afficherMap(map, ludophile);
            effetElement(ludophile, map, but);
        }
    }

    //Avancer vers l'Est
    void avancerEst(Joueur ludophile, String[][] map, Objectif but){
        if(deplacementImpossibleEst(ludophile, map)){
            kaomijiOrateurln(ROUGE + "Ce déplacement n'est pas possible !" + RESET);
            erreurAlgorithme(ludophile, map, but);
        } else {
            map[ludophile.idxL][ludophile.idxC+1] = ludophile.personnage;
            ludophile.idxC++;
            map[ludophile.idxL][ludophile.idxC-1] = CHEMIN;
            afficherMap(map, ludophile);
            effetElement(ludophile, map, but);
        }
    }

    //Avancer vers l'Ouest
    void avancerOuest(Joueur ludophile, String[][] map, Objectif but){
        if(deplacementImpossibleOuest(ludophile, map)){
            kaomijiOrateurln(ROUGE + "Ce déplacement n'est pas possible !" + RESET);
            erreurAlgorithme(ludophile, map, but);
        } else {
            map[ludophile.idxL][ludophile.idxC-1] = ludophile.personnage;
            ludophile.idxC--;
            map[ludophile.idxL][ludophile.idxC+1] = CHEMIN;
            afficherMap(map, ludophile);
            effetElement(ludophile, map, but);
        }
    }

    //Déplacement en boucle à compteur selon le choix du Joueur
    void boucleCompteur(Joueur ludophile, String[][] map, Objectif but, int idx, Selection choix){
        if(choix.nbChoix[idx] != 0){
            if(choix.nbChoix[idx] == 8 || choix.nbChoix[idx] == 6 || choix.nbChoix[idx] == 4 || choix.nbChoix[idx] == 2){
                deplacementPour(ludophile, map, but, choix, idx);
            } else {
                kaomijiOrateurln(JAUNE + "Tu ne t'es pas déplacé. Assure-toi d'appuyer sur le bon bouton pour te déplacer !" + RESET);
            }
        } 
    }

    //Déplacement en boucle while() selon le choix du Joueur
    void boucleWhile(Joueur ludophile, String[][] map, Objectif but, Selection choix, int idx){
        if(choix.direction[idx] != 0){
            if(choix.direction[idx] == 8 || choix.direction[idx] == 6 || choix.direction[idx] == 4 || choix.direction[idx] == 2){
                deplacementWhile(ludophile, map, but, choix, idx);
            } else {
                kaomijiOrateurln(JAUNE + "Tu ne t'es pas déplacé. Assure-toi d'appuyer sur le bon bouton pour te déplacer !" + RESET);
            }
        }
    }

    /* ============================================ */
    /* Création et exécution d'algorithme du Joueur */
    /* ============================================ */
    
    //Création d'algorithme
    int[] creationAlgorithme(Joueur ludophile, Selection choix){
        int idx;
        int[] idxBoucle = new int[2];
        int[] algorithm = new int[20];

        for(int i=0; i<length(algorithm); i++){
            algorithm[i] = 0;
            choix.nbCases[i] = 0;
            choix.nbChoix[i] = 0;
            choix.conditions[i] = "";
        }

        delayln(500);
        kaomijiOrateur("Quand vous serez prêt à composer votre algorithme, appuyez sur [ENTER].");
        String saisie = readString();
        println();
        
        idx = 0;
        while(!equals(saisie, "0") && idx<length(algorithm)){
            kaomijiOrateur("Choix de déplacement (" + (idx+1) + "/20): ");
            saisie = readString();
            algorithm[idx] = verificationString(saisie);

            while(algorithm[idx] == 5 || algorithm[idx] == 7 || algorithm[idx] >= 9){
                kaomijiOrateur(JAUNE + "Votre saisie ne correspond à aucun déplacement, veuillez réessayer: " + RESET);
                saisie = readString();
                algorithm[idx] = verificationString(saisie);
            }

            if(algorithm[idx] == 1){
                println("\n──────────────────────────────────────────────────────────────────────────────\n");
                idxBoucle[0] = saisiePour(choix, idxBoucle[0]);
                idxBoucle[0]++;
                println("\n──────────────────────────────────────────────────────────────────────────────\n");
            } else if(algorithm[idx] == 3){
                println("\n──────────────────────────────────────────────────────────────────────────────\n");
                idxBoucle[1] = saisieWhile(choix, idxBoucle[1]);
                idxBoucle[1]++;
                println("\n──────────────────────────────────────────────────────────────────────────────\n");
            }

            idx++;
        }
        delayln(1000);
        return algorithm;
    }

    //Convertion de chiffre en direction
    String intToDirection(int nbDirection){
        if(nbDirection == 8){
            return "⬆️   ";
        } else if(nbDirection == 2){
            return "⬇️   ";
        } else if(nbDirection == 4){
            return "⬅️   ";
        } else {
            return "➡️   ";
        }
    }

    //Confirmer si l'algorithme est bon
    int[] confirmationAlgorithme(Joueur ludophile, String[][] map, Selection choix){
        int[] algorithm = new int[20]; 
        int[] idxBoucle = new int[2];

        int idx;
        String saisie = "0";

        while(!equals(saisie, "OUI") && !equals(saisie, "O")){
            algorithm = creationAlgorithme(ludophile, choix);
            kaomijiOrateurln("Avant d'exécuter votre algorithme, confirmez que c'est bien ce que vous voulez exécuter : ");
           
            idx = 0;
            while(idx<length(algorithm) && algorithm[idx] != 0){
                if(algorithm[idx] == 8){
                    println(espacement(maitreKaomiji(nbVie)) + " • [8] : Déplacement vers le HAUT");
                } else if(algorithm[idx] == 2){
                    println(espacement(maitreKaomiji(nbVie)) + " • [2] : Déplacement vers le BAS");
                } else if(algorithm[idx] == 4){
                    println(espacement(maitreKaomiji(nbVie)) + " • [4] : Déplacement vers la GAUCHE");
                } else if(algorithm[idx] == 6){
                    println(espacement(maitreKaomiji(nbVie)) + " • [6] : Déplacement vers la DROITE");
                } else if(algorithm[idx] == 1){
                    println(espacement(maitreKaomiji(nbVie)) + " • [1] : Déplacement en boucle 'POUR' vers le " + BLEU + intToDirection(choix.nbChoix[idxBoucle[0]]) + ", " + choix.nbCases[idxBoucle[0]] + " fois" + RESET);
                    idxBoucle[0]++;
                } else if(algorithm[idx] == 3){
                    println(espacement(maitreKaomiji(nbVie)) + " • [3] : Déplacement en boucle 'TANT QUE' vers le " + BLEU + intToDirection(choix.direction[idxBoucle[1]]) + " tant que la case est un " + CHEMIN + RESET);
                    idxBoucle[1]++;
                }
                idx++;
            }
            
            kaomijiOrateur(GRAS + "Est-ce bien ce que vous souhaitez exécuter ? [Oui (O) ; Non (N)] " + RESET);
            saisie = toUpperCase(readString());

            while((!equals(saisie, "OUI") && !equals(saisie, "O")) && (!equals(saisie, "NON") && !equals(saisie, "N"))){
                kaomijiOrateur(JAUNE + "Vérifie ce que tu as écrit : tu dois choisir entre Oui (O) ou Non (N) : " + RESET);
                saisie = toUpperCase(readString());
            }

            if(equals(saisie, "NON") || equals(saisie, "N")){
                println();
                kaomijiOrateurln(VERT + "Alors recommençons !" + RESET);
                afficherMap(map, ludophile);
            } 
        }

        kaomijiOrateurln(GRAS + "Commençons l'exécution de l'algorithme !" + RESET);
        return algorithm;
    }

    //Exécution d'algorithme
    void executionAlgorithme(Joueur ludophile, String[][] map, Objectif but){
        Selection choix = newSelection();
        int[] algorithm = confirmationAlgorithme(ludophile, map, choix);
        int[] idxBoucle = new int[2];
        int idx = 0;

        while(objectifPasAtteint(ludophile, map, but) && nbVie > 0 && idx<length(algorithm) && algorithm[idx] != 0){
            deplacement(algorithm[idx], ludophile, map, but, idxBoucle[0], idxBoucle[1], choix);
            if(algorithm[idx] == 1){
                idxBoucle[0]++;
            } else if(algorithm[idx] == 3){
                idxBoucle[1]++;
            }
            idx++;
            delay(500);
        }

        if(idx == length(algorithm)){
            idx--;
        }

        if(objectifPasAtteint(ludophile, map, but) && (idx == length(algorithm)-1 || algorithm[idx] == 0) && nbVie>0){
            kaomijiOrateurln(JAUNE + "Il semblerait que ton algorithme ne soit pas correct ou que tu n'aies pas eu de chance..." + RESET);
            map[ludophile.idxL][ludophile.idxC] = CHEMIN;
            placementJoueur(map, ludophile);
            delay(1500);
            afficherMap(map, ludophile);
            kaomijiOrateurln(VERT + "N'hésite pas à le revoir et à réessayer ! Recommençons !\n" + RESET);
        }
    }

    /* ================================================================== */
    /* Tout ce qui est relatif à la création et information du personnage */
    /* ================================================================== */

    //Retournera vrai si le nom du joueur a une virgule
    boolean estVirgule(String nom){
        int idx = 0;
        while(idx<length(nom) && charAt(nom, idx) != ','){
            idx++;
        }

        if(idx == length(nom)){
            idx--;
        }

        return charAt(nom, idx) == ',';
    }

    //Donnera le nom "Nameless" si le joueur ne rentre pas de caractère dans son nom
    String verificationNom(String nom){
        int idx = 0;

        while(estVirgule(nom)){
            kaomijiOrateur(JAUNE + "Ton nom a un truc qui ne va pas, peux-tu le retaper, s'il te plaît ? " + RESET);
            nom = readString();
        }

        while(idx<length(nom) && charAt(nom, idx) == ' '){
            idx++;
        }

        if(idx<length(nom)){
            if(length(nom) > 8 && equals(substring(nom, 0, 8), "Nameless")){
                return "Nameless";
            }
            return substring(nom, idx, length(nom));
        } 

        return "Nameless";
    }

    void testVerificationNom(){
        String nom;

        nom = "";
        assertEquals("Nameless", verificationNom(nom));

        nom = "      ";
        assertEquals("Nameless", verificationNom(nom));

        nom = "Tagada";
        assertEquals("Tagada", verificationNom(nom));

        nom = "      Toto";
        assertEquals("Toto", verificationNom(nom));
    }

    //Confirmation de jouer en tant que Nameless
    void estNameless(Joueur ludophile){
        String saisie;
        if(equals(ludophile.nom, "Nameless")){
            kaomijiOrateur("Pour l’instant, ton nom (" + ludophile.nom + ") ne te permettra pas de récupérer tes données si besoin. Veux-tu le changer ? [Oui (O) ; Non (N)] : ");
            saisie = toUpperCase(readString());

            while((!equals(saisie, "O") && !equals(saisie, "OUI")) && (!equals(saisie, "N") && !equals(saisie, "NON"))){
                kaomijiOrateur(JAUNE + "Vérifie ce que tu as écrit : tu dois choisir entre Oui (O) ou Non (N) : " + RESET);
                saisie = toUpperCase(readString());
            }

            if(equals(saisie, "O") || equals(saisie, "OUI")){
                do{
                    kaomijiOrateur("Écris un nom différent de 'Nameless' : ");
                    ludophile.nom = readString();
                }while(equals(verificationNom(ludophile.nom), "Nameless"));
            }
            delay(1000);
        }
    }

    //Genre du Joueur
    String genreJoueur(Joueur ludophile){
        kaomijiOrateur("Quel est votre genre [Masculin (M); Feminin (F)] : ");
        ludophile.genre = toUpperCase(readString());
        while((!equals(ludophile.genre, "MASCULIN") && !equals(ludophile.genre, "M")) && (!equals(ludophile.genre, "FEMININ") && !equals(ludophile.genre, "F"))){
            kaomijiOrateur(ROUGE + "Non, vous devez choisir entre Masculin (ou M) et Feminin (ou F) : " + RESET);
            ludophile.genre = readString();
        }

        return ludophile.genre;
    }

    //Personnage du Joueur
    String personnageJoueur(Joueur ludophile){
        String[] personnageMasculin = new String[]{"👨","👦","👶","🌞"};
        String[] personnageFeminin = new String[]{"👩","👧","👶","🌝"};

        if(equals(ludophile.genre, "MASCULIN") || equals(ludophile.genre, "M")){
            afficherPersonnage(personnageMasculin);
            delay(500);
            ludophile.personnage = personnageMasculin[selectionPersonnage(personnageMasculin)];
        } else {
            afficherPersonnage(personnageFeminin);
            ludophile.personnage = personnageFeminin[selectionPersonnage(personnageFeminin)];
        }

        return ludophile.personnage;
    }

    //Affichage des personnages
    void afficherPersonnage(String[] personnage){
        kaomijiOrateurln("Voici les personnages qui sont à ta disposition : ");
        for(int idx=0; idx<length(personnage); idx++){
            println(espacement(kaomijiPhrase("")) + (idx+1) + " : " + personnage[idx]);
            delay(500);
        }
    }

    //Selection de personnage
    int selectionPersonnage(String[] personnage){
        kaomijiOrateur("Choisis un personnage en tapant le numéro qui lui correspond : ");
        int choix = verificationString(readString());
        while((choix>length(personnage) || choix<1)){
            kaomijiOrateur(ROUGE + "Ton choix n'est pas bon, essaie encore : " + RESET);
            choix = verificationString(readString());
        }
        return choix-1;
    }

    //Positionnement du Joueur
    String positionJoueur(Joueur ludophile, String[][] map){
        ludophile.position = "[" + (ludophile.idxL+1) + ";" + (ludophile.idxC+1) + "]";
        return ludophile.position;
    }

    void testPositionJoueur(){
        Joueur ludophile = newJoueur();
        ludophile.personnage = "👨";

        ludophile.idxL = 1;
        ludophile.idxC = 1;
        String[][] map = new String[][]{{CHEMIN, CHEMIN, CHEMIN},
                                        {CHEMIN, ludophile.personnage, CHEMIN},
                                        {CHEMIN, CHEMIN, CHEMIN}};
        assertEquals("[2;2]",positionJoueur(ludophile, map));

        ludophile.idxL = 0;
        ludophile.idxC = 0;
        map = new String[][]{{ludophile.personnage, CHEMIN, CHEMIN},
                             {CHEMIN, CHEMIN, CHEMIN},
                             {CHEMIN, CHEMIN, CHEMIN}};
        assertEquals("[1;1]",positionJoueur(ludophile, map));
    }

    //Met en String l'état du bonus : immunité au malus
    String immuniteToString(Joueur ludophile){
        if(ludophile.immunite == true){
            return "Activé";
        }
        return "Désactivé";
    }

    void testImmuniteToString(){
        Joueur ludophile = newJoueur();

        ludophile.immunite = true;
        assertEquals("Activé", immuniteToString(ludophile));

        ludophile.immunite = false;
        assertEquals("Désactivé", immuniteToString(ludophile));
    }

    //PV du joueur sous forme de coeur
    String nbCoeur(int nbLife){
        String coeur = "";

        for(int cpt=0; cpt<nbLife; cpt++){
            coeur = coeur + "❤️";
        }

        if(equals(coeur, "")){
            return "💔";
        }
        
        return coeur;
    }

    void testNbCoeur(){
        int nbLife;

        nbLife = 3;
        assertEquals("❤️❤️❤️", nbCoeur(nbLife));

        nbLife = 0;
        assertEquals("💔", nbCoeur(nbLife));
    }

    //Bouclier du joueur sous forme de bouclier
    String nbBouclier(Joueur ludophile){
        String bouclier = "";

        for(int cpt=0; cpt<ludophile.nbBouclier; cpt++){
            bouclier = bouclier + "🛡️";
        }

        if(equals(bouclier, "")){
            return "0";
        }

        return bouclier;
    }

    void testNbBouclier(){
        Joueur ludophile = newJoueur();

        ludophile.nbBouclier = 3;
        assertEquals("🛡️🛡️🛡️", nbBouclier(ludophile));

        ludophile.nbBouclier = 0;
        assertEquals("0", nbBouclier(ludophile));
    }

    //Affichage des informations (Nom, PV, Coordonnées, Reussite...)
    void informationJoueur(Joueur ludophile, String[][] map){
        print(GRAS + ludophile.nom + " - PV: " + nbCoeur(nbVie) + " ; Coordonées: " + positionJoueur(ludophile, map) + " ; Bouclier: " + nbBouclier(ludophile) + " ; Immunité: " + immuniteToString(ludophile) + " ; Reussite: " + ludophile.nbReussite + "\n" + RESET);
    }

    /* ======================================================= */
    /* Tout ce qui est relatif au effet des éléments de la map */
    /* ======================================================= */
    
    //Regroupe tous les effets des éléments 
    void effetElement(Joueur ludophile, String[][] map, Objectif but){
        effetBombe(ludophile, map);
        effetArbre(ludophile, map);
        effetLave(ludophile, map);
        effetCarte(ludophile, map, but);
    }

    //Retournera vrai si le Joueur a atteint l'objectif
    boolean objectifPasAtteint(Joueur ludophile, String[][] map, Objectif but){
        return equals(map[but.idxL][but.idxC], but.DRAPEAU);
    }

    void testObjectifPasAtteint(){
        Joueur ludophile = newJoueur();
        Objectif but = newObjectif();
        String[][] map;

        ludophile.personnage = "👨";

        but.idxL = 0;
        but.idxC = 1;

        map = new String[][]{{CHEMIN,but.DRAPEAU,CHEMIN},
                             {ludophile.personnage, CHEMIN, CHEMIN},
                             {CHEMIN,CHEMIN,CHEMIN}};
        assertTrue(objectifPasAtteint(ludophile, map, but));

        map = new String[][]{{CHEMIN,ludophile.personnage,CHEMIN},
                             {CHEMIN, CHEMIN, CHEMIN},
                             {CHEMIN,CHEMIN,CHEMIN}};
        assertFalse(objectifPasAtteint(ludophile, map, but));
    }

    //Effet de la Bombe
    void effetBombe(Joueur ludophile, String[][] map){
        final String EXPLOSION = "💥";

        //Si la bombe se trouve au-dessus OU en-dessous du joueur
        if((idxBombe[0] != -1 && idxBombe[1] != -1) && equals(map[idxBombe[0]][idxBombe[1]], ludophile.personnage)){
            delay(500);
            map[idxBombe[0]][idxBombe[1]] = EXPLOSION;
            
            if(ludophile.nbBouclier>0){
                ludophile.nbBouclier--;
            } else {
                nbVie--;
            }
            
            afficherMap(map, ludophile);
            println();
            kaomijiOrateurln(GRAS + ROUGE + "Attention ! Tu as touché une bombe, fais super gaffe !" + RESET);
            delay(500);
            map[idxBombe[0]][idxBombe[1]] = ludophile.personnage;
            afficherMap(map, ludophile);
            idxBombe[0] = -1;
            idxBombe[1] = -1;
        }

        if(ludophile.idxL>0 && equals(map[ludophile.idxL-1][ludophile.idxC], BOMBE)){
            idxBombe[0] = ludophile.idxL-1;
            idxBombe[1] = ludophile.idxC;
        }

        if(ludophile.idxL<length(map)-1 && equals(map[ludophile.idxL+1][ludophile.idxC], BOMBE)){
            idxBombe[0] = ludophile.idxL+1;
            idxBombe[1] = ludophile.idxC;
        }

        //Si la bombe se trouve à droite OU à gauche du joueur
        if((idxBombe[2] != -1 && idxBombe[3] != -1) && equals(map[idxBombe[2]][idxBombe[3]], ludophile.personnage)){
            delay(500);
            map[idxBombe[2]][idxBombe[3]] = EXPLOSION;
            
            if(ludophile.nbBouclier>0){
                ludophile.nbBouclier--;
            } else {
                nbVie--;
            }
            
            afficherMap(map, ludophile);
            println();
            kaomijiOrateurln(GRAS + ROUGE + "Attention ! Tu as touché une bombe, fais super gaffe !" + RESET);
            delay(500);
            map[idxBombe[2]][idxBombe[3]] = ludophile.personnage;
            afficherMap(map, ludophile);
            idxBombe[2] = -1;
            idxBombe[3] = -1;
        }

        if(ludophile.idxC<length(map)-1 && equals(map[ludophile.idxL][ludophile.idxC+1], BOMBE)){
            idxBombe[2] = ludophile.idxL;
            idxBombe[3] = ludophile.idxC+1;
        }

        if(ludophile.idxC>0 && equals(map[ludophile.idxL][ludophile.idxC-1], BOMBE)){
            idxBombe[2] = ludophile.idxL;
            idxBombe[3] = ludophile.idxC-1;
        }

        //Pour éviter que les duplications des effets
        if(idxBombe[0] != -1 && idxBombe[1] != -1){
            idxBombe[2] = -1;
            idxBombe[3] = -1;
        } else {
            idxBombe[0] = -1;
            idxBombe[1] = -1;
        }
    }

    //Effet des Arbres
    void effetArbre(Joueur ludophile, String[][] map){
        final String COCO = "🥥";
        double probabilite = random();

        //Si l'arbre se trouve au-dessus OU en-dessous du joueur
        if((idxArbre[0] != -1 && idxArbre[1] != -1) && equals(map[idxArbre[0]][idxArbre[1]], ludophile.personnage) && probabilite<0.3){
            delay(500);
            map[idxArbre[0]][idxArbre[1]] = COCO;
            
            if(ludophile.nbBouclier>0){
                ludophile.nbBouclier--;
            } else {
                nbVie--;
            }
            
            afficherMap(map, ludophile);
            println();
            kaomijiOrateurln(GRAS + ROUGE + "Oups ! Une énorme noix de coco est tombée sur ta tête, vraiment pas de chance !" + RESET);
            delay(500);
            map[idxArbre[0]][idxArbre[1]] = ludophile.personnage;
            afficherMap(map, ludophile);
            idxArbre[0] = -1;
            idxArbre[1] = -1;
        }

        if(ludophile.idxL>0 && equals(map[ludophile.idxL-1][ludophile.idxC], ARBRE)){
            idxArbre[0] = ludophile.idxL-1;
            idxArbre[1] = ludophile.idxC;
        }

        if(ludophile.idxL<length(map)-1 && equals(map[ludophile.idxL+1][ludophile.idxC], ARBRE)){
            idxArbre[0] = ludophile.idxL+1;
            idxArbre[1] = ludophile.idxC;
        }

        //Si l'arbre se trouve à droite OU à gauche du joueur
        if((idxArbre[2] != -1 && idxArbre[3] != -1) && equals(map[idxArbre[2]][idxArbre[3]], ludophile.personnage) && probabilite<0.3){
            delay(500);
            map[idxArbre[2]][idxArbre[3]] = COCO;
            
            if(ludophile.nbBouclier>0){
                ludophile.nbBouclier--;
            } else {
                nbVie--;
            }
            
            afficherMap(map, ludophile);
            println();
            kaomijiOrateurln(GRAS + ROUGE + "Oups ! Une énorme noix de coco est tombée sur ta tête, vraiment pas de chance !" + RESET);
            delay(500);
            map[idxArbre[2]][idxArbre[3]] = ludophile.personnage;
            afficherMap(map, ludophile);
            idxArbre[2] = -1;
            idxArbre[3] = -1;
        }

        
        if((ludophile.idxC<length(map)-1 && equals(map[ludophile.idxL][ludophile.idxC+1], ARBRE))){
            idxArbre[2] = ludophile.idxL;
            idxArbre[3] = ludophile.idxC+1;
        }

        if(ludophile.idxC>0 && equals(map[ludophile.idxL][ludophile.idxC-1], ARBRE)){
            idxArbre[2] = ludophile.idxL;
            idxArbre[3] = ludophile.idxC-1;
        }

        //Pour éviter que les duplications des effets
        if(idxArbre[0] != -1 && idxArbre[1] != -1){
            idxArbre[2] = -1;
            idxArbre[3] = -1;
        } else {
            idxArbre[0] = -1;
            idxArbre[1] = -1;
        }
    }

    //Effet de la lave
    void effetLave(Joueur ludophile, String[][] map){
        final String BRULE = "🔥";

        //Si la lave se trouve au-dessus OU en-dessous du joueur
        if((idxLave[0] != -1 && idxLave[1] != -1) && equals(map[idxLave[0]][idxLave[1]], ludophile.personnage)){
            delay(500);
            map[idxLave[0]][idxLave[1]] = BRULE;
            
            for(int cpt=0; cpt<5; cpt++){
                if(ludophile.nbBouclier>0){
                    ludophile.nbBouclier--;
                } else {
                    nbVie--;
                }
            }

            afficherMap(map, ludophile);
            println();
            kaomijiOrateurln(GRAS + ROUGE + "C'est dangereux de sauter dans la lave, tu es malade de faire ça !" + RESET);
            delay(500);
            map[idxLave[0]][idxLave[1]] = ludophile.personnage;
            afficherMap(map, ludophile);
            idxLave[0] = -1;
            idxLave[1] = -1;
        }

        if(ludophile.idxL>0 && equals(map[ludophile.idxL-1][ludophile.idxC], LAVE)){
            idxLave[0] = ludophile.idxL-1;
            idxLave[1] = ludophile.idxC;
        }

        if(ludophile.idxL<length(map)-1 && equals(map[ludophile.idxL+1][ludophile.idxC], LAVE)){
            idxLave[0] = ludophile.idxL+1;
            idxLave[1] = ludophile.idxC;
        }

        //Si la lave se trouve à droite OU à gauche du joueur
        if((idxLave[2] != -1 && idxLave[3] != -1) && equals(map[idxLave[2]][idxLave[3]], ludophile.personnage)){
            delay(500);
            map[idxLave[2]][idxLave[3]] = BRULE;
            
            for(int cpt=0; cpt<5; cpt++){
                if(ludophile.nbBouclier>0){
                    ludophile.nbBouclier--;
                } else {
                    nbVie--;
                }
            }

            afficherMap(map, ludophile);
            println();
            kaomijiOrateurln(GRAS + ROUGE + "C'est dangereux de sauter dans la lave, tu es malade de faire ça !" + RESET);
            delay(500);
            map[idxLave[2]][idxLave[3]] = ludophile.personnage;
            afficherMap(map, ludophile);
            idxLave[2] = -1;
            idxLave[3] = -1;
        }

        if((ludophile.idxC<length(map)-1 && equals(map[ludophile.idxL][ludophile.idxC+1], LAVE))){
            idxLave[2] = ludophile.idxL;
            idxLave[3] = ludophile.idxC+1;
        }

        if(ludophile.idxC>0 && equals(map[ludophile.idxL][ludophile.idxC-1], LAVE)){
            idxLave[2] = ludophile.idxL;
            idxLave[3] = ludophile.idxC-1;
        }

        //Pour éviter que les duplications des effets
        if(idxLave[0] != -1 && idxLave[1] != -1){
            idxLave[2] = -1;
            idxLave[3] = -1;
        } else {
            idxLave[0] = -1;
            idxLave[1] = -1;
        }
    }

    //Saisie du joueur pour les effets des cartes
    String saisieBivalent(){
        String decision;
        println("\n──────────────────────────────────────────────────────────────────────────────\n");
        kaomijiOrateurln(BLEU + "Tu as tiré une carte événement ! Attention, c’est du 50/50, tu peux soit gagner un bonus, soit subir un malus, tout dépend de ta réponse !" + RESET);
        kaomijiOrateurln("Je pense à un chiffre entre 1 et 10, tu crois qu'il est pair ou impair ?");
        kaomijiOrateur("Choix [PAIR ; IMPAIR]: ");
        decision = toUpperCase(readString());

        while(!equals(decision, "PAIR") && !equals(decision, "IMPAIR")){
            kaomijiOrateur(ROUGE + "Non, tu dois choisir entre [PAIR, IMPAIR] : " + RESET);
            decision = readString();
        }
        return decision;
    }

    //Effet des cartes
    void effetCarte(Joueur ludophile, String[][] map, Objectif but){
        String decision;
        int chiffre = (int) (random()*10)+1;

        //Si la carte se trouve au-dessus OU en-dessous du joueur
        if((idxCarte[0] != -1 && idxCarte[1] != -1) && equals(map[idxCarte[0]][idxCarte[1]], ludophile.personnage)){
            decision = saisieBivalent();
            kaomijiOrateurln("Le chiffre auquel je pensais est : " + chiffre);
            if((chiffre%2 == 0 && equals(decision, "PAIR")) || (chiffre%2 == 1 && equals(decision, "IMPAIR"))){
                kaomijiOrateurln(GRAS + VERT + "TU AS UN BONUS !" + RESET);
                bonus(ludophile, map);
            } else {
                kaomijiOrateurln(GRAS + ROUGE + "TU AS UN MALUS !" + RESET);
                malus(ludophile, map, but);
            }

            idxCarte[0] = -1;
            idxCarte[1] = -1;
        }

        if(ludophile.idxL>0 && equals(map[ludophile.idxL-1][ludophile.idxC], CARTE)){
            idxCarte[0] = ludophile.idxL-1;
            idxCarte[1] = ludophile.idxC;
        }
        
        if(ludophile.idxL<length(map)-1 && equals(map[ludophile.idxL+1][ludophile.idxC], CARTE)){
            idxCarte[0] = ludophile.idxL+1;
            idxCarte[1] = ludophile.idxC;
        }

        //Si la carte se trouve à droite OU à gauche du joueur
        if((idxCarte[2] != -1 && idxCarte[3] != -1) && equals(map[idxCarte[2]][idxCarte[3]], ludophile.personnage)){
            decision = saisieBivalent();
            kaomijiOrateurln("Le chiffre auquel je pensais est : " + chiffre);

            if((chiffre%2 == 0 && equals(decision, "PAIR")) || (chiffre%2 == 1 && equals(decision, "IMPAIR"))){
                kaomijiOrateurln(GRAS + VERT + "TU AS UN BONUS !" + RESET);
                bonus(ludophile, map);
            } else {
                kaomijiOrateurln(GRAS + ROUGE + "TU AS UN MALUS !" + RESET);
                malus(ludophile, map, but);
            }

            idxCarte[2] = -1;
            idxCarte[3] = -1;
        }
        
        if(ludophile.idxC<length(map)-1 && equals(map[ludophile.idxL][ludophile.idxC+1], CARTE)){
            idxCarte[2] = ludophile.idxL;
            idxCarte[3] = ludophile.idxC+1;
        }
        
        if(ludophile.idxC>0 && equals(map[ludophile.idxL][ludophile.idxC-1], CARTE)){
            idxCarte[2] = ludophile.idxL;
            idxCarte[3] = ludophile.idxC-1;
        }

        //Pour éviter que les duplications des effets
        if(idxCarte[0] != -1 && idxCarte[1] != -1){
            idxCarte[2] = -1;
            idxCarte[3] = -1;
        } else {
            idxCarte[0] = -1;
            idxCarte[1] = -1;
        }
    }

    /* ========================================= */
    /* Tout ce qui concerne les bonus, les malus */
    /* ========================================= */

    //Les bonus
    void bonus(Joueur ludophile, String[][] map){
        String saisie;
        int choix;

        kaomijiOrateurln("Choisis ce que tu veux parmi les bonus suivants ! (Tant que tu n'as pas atteint leur max.) : ");
        println(espacement(kaomijiPhrase("")) + "0: Rien.");
        println(espacement(kaomijiPhrase("")) + "1: Récuperez des PV (max. 10): ❤️");
        println(espacement(kaomijiPhrase("")) + "2: Obtenir un bouclier (max. 5): 🛡️");
        println(espacement(kaomijiPhrase("")) + "3: Obtenir une immunité contre des malus (max. 1): 💊");
        print("Choix: ");
        saisie = readString();

        while(stringtoInt(saisie)<0 || stringtoInt(saisie)>3){
            kaomijiOrateur(ROUGE + "Ton choix n'est pas bon, essaie encore : " + RESET);
            saisie = readString();
        }

        choix = stringtoInt(saisie);

        if(choix == 0){
            kaomijiOrateurln("Pas de bonus ? Alors, continuons !");
            afficherMap(map, ludophile);
        } else if(choix == 1){
            if(nbVie < 10){
                kaomijiOrateurln("Voilà, je te file un PV !");
                map[ludophile.idxL][ludophile.idxC] = "🥰";
                nbVie++;
                afficherMap(map, ludophile);
                delay(1000);
                map[ludophile.idxL][ludophile.idxC] = ludophile.personnage;
                afficherMap(map, ludophile);
            } else {
                kaomijiOrateurln("Ton PV est déjà au max... On recommence !");
                bonus(ludophile, map);
            }
        } else if(choix == 2){
            if(ludophile.nbBouclier < 5){
                kaomijiOrateurln("Voilà, je te file un bouclier !");
                map[ludophile.idxL][ludophile.idxC] = "🛡️";
                ludophile.nbBouclier++;
                afficherMap(map, ludophile);
                delay(1000);
                map[ludophile.idxL][ludophile.idxC] = ludophile.personnage;
                afficherMap(map, ludophile);
            } else {
                kaomijiOrateurln("Ton nombre de bouclier est déjà au max... On recommence !");
                bonus(ludophile, map);
            }
        } else if(choix == 3){
            kaomijiOrateurln(JAUNE + "Activation de l'immunité..." + RESET);
            map[ludophile.idxL][ludophile.idxC] = "💊";
            ludophile.immunite = true;
            afficherMap(map, ludophile);
            delay(1000);
            map[ludophile.idxL][ludophile.idxC] = ludophile.personnage;
            afficherMap(map, ludophile);
        }
        println("\n──────────────────────────────────────────────────────────────────────────────\n");
    }

    //Les malus
    void malus(Joueur ludophile, String[][] map, Objectif but){
        kaomijiOrateurln("Désolé, mais tu as un malus !");
        int random = (int) (random()*2)+1;
        delay(500);

        if(ludophile.immunite == true){
            kaomijiOrateurln(GRAS + VERT + "Malheureusement... Tu as une immunité contre les malus... Quelle tristesse !" + RESET);
            kaomijiOrateurln(JAUNE + "Désactivation de l'immunité..." + RESET);
            ludophile.immunite = false;
            afficherMap(map, ludophile);
        } else {
            if(random == 1){
                kaomijiOrateurln(ROUGE + "Un joli petit météore va te faire plaisir ! (Elle traverse le bouclier)" + RESET);
                map[ludophile.idxL][ludophile.idxC] = "☄️";
                afficherMap(map, ludophile);
                delay(1000);
                map[ludophile.idxL][ludophile.idxC] = "💥";
                nbVie = nbVie - 3;
                afficherMap(map, ludophile);
                delay(1000);
                map[ludophile.idxL][ludophile.idxC] = ludophile.personnage;
                afficherMap(map, ludophile);
                kaomijiOrateurln(JAUNE + "Attention, tu as perdu 3 ❤️\n" + RESET);
            } else if(random == 2){
                kaomijiOrateurln(ROUGE + "Téléportation totalement aléatoire !" + RESET);
                map[ludophile.idxL][ludophile.idxC] = CHEMIN;
                do{
                    ludophile.idxL = (int)(random()*length(map,1)-1);
                    ludophile.idxC = (int)(random()*length(map,2)-1);
                }while(ludophile.idxL == but.idxL || ludophile.idxC == but.idxC);
                map[ludophile.idxL][ludophile.idxC] = ludophile.personnage;
                afficherMap(map, ludophile);
            }
        }
        println("\n──────────────────────────────────────────────────────────────────────────────");
        afficherMap(map, ludophile);
    }

    /* ===================================================== */
    /* Tout ce qui concerne begin() lors du lancement du jeu */
    /* ===================================================== */
    void begin(Joueur ludophile, Objectif but){
        final String ASCII = "ressources/File/VivreOuSurvivre.txt";
        lecteurFichier(ASCII);
        creationPersonnage(ludophile);
        if(ludophile.tutoriel == true){
            tutoriel(ludophile, but);
        }
    }

    void lecteurFichier(String chemin){
        File fichier = newFile(chemin);
		while(ready(fichier)){
			println(readLine(fichier));
		}
        delay(1000);
    }

    //Création du personnage du joueur
    void creationPersonnage(Joueur ludophile){
        kaomijiOrateurln(GRAS + "Bienvenue dans VivreOuSurvivre ! Dans ce jeu, tu vas apprendre les bases des algorithmes en t'amusant." + RESET);
        kaomijiOrateur("Je me présente, je suis le maître du jeu : Kaomiji, ton super compagnon ! Et toi, qui es-tu ? ");
        ludophile.nom = verificationNom(readString());
        delay(1000);
        estNameless(ludophile);
        if(!equals(ludophile.nom, "Nameless")){
            if(length(data(csvData))>3 && estNomExistant(ludophile, csvData)){
                println();
                restoreLudophile(ludophile, csvData);
            } else {
                println();
                kaomijiOrateur(BLEU + "Afin de pouvoir revenir là où tu t'es arrêté, je te demande d'entrer un mot que seul toi tu connais : " + RESET);
                ludophile.mdp = readString();
                delay(1000);
                genreJoueur(ludophile);
                kaomijiOrateurln(JAUNE + "Enregistrement de tes données en cours..." + RESET);
                saveData(ludophile, csvData);
                kaomijiOrateurln(VERT + "Enregistrement terminé !" + RESET);
                ludophile.id = idLudophile(csvData);
            }
        } else {
            genreJoueur(ludophile);
        }
        println();
        kaomijiOrateurln("Avant de commencer/continuer à t'apprendre les bases des algorithmes, il faut d'abord créer ton personnage.");
        personnageJoueur(ludophile);
        delay(1000);
    }

    //Permet un saut à la ligne + delay
    void delayln(int temps){
        println();
        delay(temps);
    }

    //Tutoriel Global
    void tutoriel(Joueur ludophile, Objectif but){
        String choix;
        String[][] map = new String[5][5];
        delay(1000);
        println("\n──────────────────────────────────────────────────────────────────────────────\n");
        kaomijiOrateurln("Ce que tu dois savoir ET retenir, " + BLEU + "c'est que les ordinateurs font exactement TOUT ce qu'on leur dit" + RESET + ", sans poser de questions.");
        kaomijiOrateurln("Pour ce tutoriel, je te conseille" + BLEU + " d'utiliser le pavé numérique " + RESET + "pour entrer les valeurs des déplacements qui lui correspondent.");
        kaomijiOrateur(JAUNE + "PS: Tu ne gagnes pas de points de réussite. ;^;\n\n" + RESET + "Appuie sur [ENTER] pour démarrer le tutoriel ! 🚀 ");
        choix = readString();
        delayln(1000);
        tutorielBase(ludophile, but, map);
        delayln(1000);
        tutorielBoucle(ludophile, but, map);
        delayln(1000);
        tutorielAlgorithme(ludophile, but, map);
        delayln(1000);
        kaomijiOrateurln("Pour conclure ce tutoriel, tu as découvert les outils à ta disposition (avancer, reculer, droite, gauche, boucle, ...) ainsi que ce qu'est un algorithme.");
        kaomijiOrateurln(GRAS + BLEU + "Pour rappel, un algorithme est une suite d'instructions à suivre dans un ordre précis pour atteindre ton objectif." + RESET);
        kaomijiOrateurln("Grâce à ces outils, tu peux maintenant créer des algorithmes pour résoudre différents problèmes ou accomplir des tâches de manière logique et organisée.\n");
        kaomijiOrateur(GRAS + "Si tu es prêt à commencer réellement le jeu, appuie sur la touche [ENTER] de ton clavier !" + RESET);
        choix = readString();
        ludophile.tutoriel = false;
        if(!equals(ludophile.nom, "Nameless")){
            adjustLudophile(ludophile, csvData);
        }
        println("\n──────────────────────────────────────────────────────────────────────────────\n");
    }



    /* ======================================================== */
    /* Tout ce qui concerne tutoriel() lors du lancement du jeu */
    /* ======================================================== */

    //Tutoriel de base (avancer, reculer, etc.)
    void tutorielBase(Joueur ludophile, Objectif but, String[][] map){
        println("\n──────────────────────────────────────────────────────────────────────────────\n");
        avancerTutoriel(ludophile, but, map);
        println("──────────────────────────────────────────────────────────────────────────────\n");
        droiteTutoriel(ludophile, but, map);
        println("──────────────────────────────────────────────────────────────────────────────\n");
        gaucheTutoriel(ludophile, but, map);
        println("──────────────────────────────────────────────────────────────────────────────\n");
        basTutoriel(ludophile, but, map);
        println("──────────────────────────────────────────────────────────────────────────────\n");
        delay(1000);
        challengeTutorielBase(ludophile, but);
        idxElement();
        println("──────────────────────────────────────────────────────────────────────────────\n");
    }

    //Tutoriel de boucle
    void tutorielBoucle(Joueur ludophile, Objectif but, String[][] map){
        String choix;
        kaomijiOrateur(BLEU + "La boucle \"Pour\" : C'est comme quand tu fais une tâche plusieurs fois.\n" + RESET + espacement(kaomijiPhrase("")) + "Par exemple, \"Fais ceci 5 fois\". Tu répètes une action un nombre précis de fois.\n\nAppuie sur [ENTER] pour démarrer le tutoriel ! 🚀 ");
        choix = readString();
        boucleCompteurTutoriel(ludophile, but, map);
        println("\n──────────────────────────────────────────────────────────────────────────────\n");
        challengeTutorielPour(ludophile, but);
        idxElement();
        println("──────────────────────────────────────────────────────────────────────────────\n");
        delay(1000);
        kaomijiOrateur(BLEU + "La boucle \"tant que\" : C'est quand tu fais quelque chose encore et encore, tant qu'une condition est vraie.\n" + RESET + espacement(kaomijiPhrase("")) + "Par exemple, \"Continue de sauter tant que tu n'as pas touché le sol\". Tu répètes jusqu'à ce que ça change.\n\nAppuie sur [ENTER] pour démarrer le tutoriel ! 🚀 ");
        choix = readString();
        boucleWhileTutoriel(ludophile, but, map);
        println("──────────────────────────────────────────────────────────────────────────────\n");
        challengeTutorielWhile(ludophile, but);
        idxElement();
        println("──────────────────────────────────────────────────────────────────────────────\n");
    }

    //Tutoriel d'algorithme
    void tutorielAlgorithme(Joueur ludophile, Objectif but, String[][] map){
        String choix;
        kaomijiOrateurln("Un algorithme, c'est comme une recette de cuisine. " + BLEU + "C'est une liste d'étapes à suivre pour accomplir quelque chose." + RESET);
        kaomijiOrateurln("Par exemple, si tu veux faire un gâteau, tu suis les étapes de la recette : mélanger les ingrédients, cuire au four, etc.");
        kaomijiOrateurln(GRAS + BLEU + "De la même manière, un algorithme te dit quoi faire, dans quel ordre, pour résoudre un problème ou accomplir une tâche." + RESET);
        kaomijiOrateurln("C’est un peu comme un guide ou une carte pour t'aider à arriver à ton objectif !\n");
        kaomijiOrateur("Si tu es prêt à commencer les algorithmes, appuie sur la touche [ENTER] de ton clavier ! ");
        choix = readString();
        delayln(1000);
        tutorielAlgo(ludophile, but, map);
        println("──────────────────────────────────────────────────────────────────────────────\n");
        challengeTutorielAlgorithme(ludophile, but);
        idxElement();
        println("──────────────────────────────────────────────────────────────────────────────\n");
    }

    // Génération des maps du tuto
    void mapTutoriel(Joueur ludophile, Objectif but, String[][] map, int[] idxL, int[] idxC){
        remplissageMap(map);
        but.idxL = idxL[0];
        but.idxC = idxC[0];
        ludophile.idxL = idxL[1];
        ludophile.idxC = idxC[1];
        map[but.idxL][but.idxC] = but.DRAPEAU;
        map[ludophile.idxL][ludophile.idxC] = ludophile.personnage;
    }

    //Tutoriel pour avancer
    void avancerTutoriel(Joueur ludophile, Objectif but, String[][] map){
        Selection choix = newSelection();
        mapTutoriel(ludophile, but, map, new int[]{length(map,1)/2, length(map,1)-1}, new int[]{length(map,2)/2, length(map,2)/2});

        kaomijiOrateurln("On va commencer doucement. Avance jusqu'à atteindre le drapeau rouge !");
        kaomijiOrateurln("Pour t'entraîner, appuie sur la touche [8] puis [ENTER] du clavier pour avancer !");
        afficherMap(map, ludophile);

        while(objectifPasAtteint(ludophile, map, but)){
            print("\n[8]:⬆️ \nChoix : ");
            int saisie = verificationString(readString());
            if(saisie == 8){
                deplacement(saisie, ludophile, map, but, 0, 0, choix);
            }else{
                kaomijiOrateurln(JAUNE + "Tu ne t'es pas déplacé. Assure-toi d'appuyer sur le bon bouton pour te déplacer !" + RESET);
            }
        }
        delayln(1000);
        kaomijiOrateurln(VERT + "Félicitations ! Maintenant, on passe aux déplacements vers la droite.\n" + RESET);
    }

    //Tutoriel pour déplacement droite
    void droiteTutoriel(Joueur ludophile, Objectif but, String[][] map){
        Selection choix = newSelection();
        mapTutoriel(ludophile, but, map, new int[]{0, length(map,1)-1}, new int[]{length(map,2)-1, length(map,2)/2});

        kaomijiOrateurln("Déplace-toi jusqu'à ce que tu atteignes le drapeau rouge !");
        kaomijiOrateurln("Pour t'entraîner, appuie sur la touche [8] ou [6] puis [ENTER] du clavier pour te déplacer!");
        afficherMap(map, ludophile);

        while(objectifPasAtteint(ludophile, map, but)){
            print("\n[8]:⬆️   ; [6]:➡️\nChoix : ");
            int saisie = verificationString(readString());
            if(saisie == 8 || saisie == 6){
                deplacement(saisie, ludophile, map, but, 0, 0, choix);
            }else{
                kaomijiOrateurln(JAUNE + "Tu ne t'es pas déplacé. Assure-toi d'appuyer sur le bon bouton pour te déplacer !" + RESET);
            }
        }
        delayln(1000);
        kaomijiOrateurln(VERT + "Félicitations ! Maintenant, on passe aux déplacements vers la gauche.\n" + RESET);
    }

    //Tutoriel déplacement vers la gauche
    void gaucheTutoriel(Joueur ludophile, Objectif but, String[][] map){
        Selection choix = newSelection();
        mapTutoriel(ludophile, but, map, new int[]{0, length(map,1)-1}, new int[]{0, length(map,2)/2});

        kaomijiOrateurln("Déplace-toi jusqu'à ce que tu atteignes le drapeau rouge !");
        kaomijiOrateurln("Pour t'entraîner, appuie sur la touche [8] ou [6] ou [4] puis [ENTER] du clavier pour te déplacer!");
        afficherMap(map, ludophile);

        while(objectifPasAtteint(ludophile, map, but)){
            print("\n[8]:⬆️   ; [6]:➡️   ; [4]:⬅️\nChoix : ");
            int saisie = verificationString(readString());
            if(saisie == 8 || saisie == 6 || saisie == 4){
                deplacement(saisie, ludophile, map, but, 0, 0, choix);
            }else{
                kaomijiOrateurln(JAUNE + "Tu ne t'es pas déplacé. Assure-toi d'appuyer sur le bon bouton pour te déplacer !" + RESET);
            }
        }
        delayln(1000);
        kaomijiOrateurln(VERT + "Félicitations ! Maintenant, on passe aux déplacements vers le bas.\n" + RESET);
    }

    //Tutoriel déplacement vers le bas
    void basTutoriel(Joueur ludophile, Objectif but, String[][] map){
        Selection choix = newSelection();
        mapTutoriel(ludophile, but, map, new int[]{length(map,1)-1, 0}, new int[]{length(map,2)-1, 0});

        kaomijiOrateurln("Déplace-toi jusqu'à ce que tu atteignes le drapeau rouge !");
        kaomijiOrateurln("Pour t'entraîner, appuie sur la touche [8] ou [6] ou [4] ou [2] puis [ENTER] du clavier pour te déplacer!");
        afficherMap(map, ludophile);

        while(objectifPasAtteint(ludophile, map, but)){
            print("\n[8]:⬆️   ; [6]:➡️   ; [4]:⬅️   ; [2]:⬇️\nChoix : ");
            int saisie = verificationString(readString());
            if(saisie == 8 || saisie == 6 || saisie == 4 || saisie == 2){
                deplacement(saisie, ludophile, map, but, 0, 0, choix);
            }else{
                kaomijiOrateurln(JAUNE + "Tu ne t'es pas déplacé. Assure-toi d'appuyer sur le bon bouton pour te déplacer !" + RESET);
            }
        }
        delayln(1000);
        kaomijiOrateurln(VERT + "Félicitations ! Maintenant, on va t'évaluer à travers un épreuve. (Pas de panique, tu ne peux pas mourir durant le tutoriel.)\n" + RESET);
    }

    //Challenge 1 (Avancer, Reculer, Droite, Gauche)
    void challengeTutorielBase(Joueur ludophile, Objectif but){
        String[][] map = new String[5][5];
        Selection choix = newSelection();
        for(int cpt=0; cpt<3; cpt++){
            kaomijiOrateurln(BLEU + "C'est parti pour le défi numéro " + (cpt+1) + "/3 !" + RESET);
            initialisationMap(map, ludophile, but);
            afficherMap(map, ludophile);
            while(objectifPasAtteint(ludophile, map, but)){
                print("\n[8]:⬆️   ; [6]:➡️   ; [4]:⬅️   ; [2]:⬇️\nChoix : ");
                int saisie = verificationString(readString());
                if(saisie == 8 || saisie == 6 || saisie == 4 || saisie == 2){
                    deplacement(saisie, ludophile, map, but, 0, 0, choix);
                }else{
                    kaomijiOrateurln(JAUNE + "Tu ne t'es pas déplacé. Assure-toi d'appuyer sur le bon bouton pour te déplacer !" + RESET);
                }
            }
            println();
        }
        kaomijiOrateurln(VERT + "Bravo, champion(ne) ! Tu as réussi ce challenge en perdant l'équivalent de " + (10-nbVie) +" PV !" + RESET);
        nbVie = 10;
    }

    //Tutoriel déplacement en boucle à compteur
    void boucleCompteurTutoriel(Joueur ludophile, Objectif but, String[][] map){
        Selection choix = newSelection();
        int idxBoucle = 0;
        mapTutoriel(ludophile, but, map, new int[]{length(map,1)-1, 0}, new int[]{length(map,2)-1, 0});
        println();
        kaomijiOrateurln("Déplace-toi jusqu'à ce que tu atteignes le drapeau rouge !");
        kaomijiOrateurln("Pour t'entraîner, appuie sur la touche [1] puis [ENTER] du clavier pour choisir un déplacement et le nombre de répétition !");
        afficherMap(map, ludophile);

        while(objectifPasAtteint(ludophile, map, but)){
            print("\n[1]:🔁\nChoix : ");
            int saisie = verificationString(readString());
            if(saisie == 1){
                idxBoucle = saisiePour(choix, idxBoucle);
                boucleCompteur(ludophile, map, but, idxBoucle, choix);
                idxBoucle++;
            }else{
                kaomijiOrateurln(JAUNE + "Tu ne t'es pas déplacé. Assure-toi d'appuyer sur le bon bouton pour te déplacer !" + RESET);
            }
        }
        delayln(1000);
        kaomijiOrateurln(VERT + "Félicitations ! Est-ce que ce n'est pas plus facile d'utiliser des boucles pour se déplacer ?" + RESET);
        kaomijiOrateurln("Maintenant, on va t'évaluer à travers une épreuve ! (Pas de panique, tu ne peux pas mourir durant le tutoriel.)\n");
    }

    //Challenge 2 (Boucle Pour)
    void challengeTutorielPour(Joueur ludophile, Objectif but){
        String[][] map = new String[10][10];
        Selection choix = newSelection();
        int idxBoucle = 0;
        for(int cpt=0; cpt<3; cpt++){
            kaomijiOrateurln(BLEU + "C'est parti pour le défi numéro " + (cpt+1) + "/3 !" + RESET);
            initialisationMap(map, ludophile, but);
            afficherMap(map, ludophile);
            while(objectifPasAtteint(ludophile, map, but)){
                print("\n[8]:⬆️   ; [6]:➡️   ; [4]:⬅️   ; [2]:⬇️   ; [1]:🔁\nChoix : ");
                int saisie = verificationString(readString());
                if(saisie == 8 || saisie == 6 || saisie == 4 || saisie == 2 || saisie == 1){
                    if(saisie == 1){
                        idxBoucle = saisiePour(choix, idxBoucle);
                        boucleCompteur(ludophile, map, but, idxBoucle, choix);
                        idxBoucle++;
                    } else {
                        deplacement(saisie, ludophile, map, but, idxBoucle, 0, choix);
                    }
                }else{
                    kaomijiOrateurln(JAUNE + "Tu ne t'es pas déplacé. Assure-toi d'appuyer sur le bon bouton pour te déplacer !" + RESET);
                }
                println();
            }
        }
        kaomijiOrateurln(VERT + "Bravo, champion(ne) ! Tu as réussi ce challenge en perdant l'équivalent de " + (10-nbVie) +" PV !" + RESET);
        nbVie = 10;
    }

    //Boucle while tutoriel
    void boucleWhileTutoriel(Joueur ludophile, Objectif but, String[][] map){
        Selection choix = newSelection();
        int idxBoucle = 0;
        mapTutoriel(ludophile, but, map, new int[]{length(map,1)-1, 0}, new int[]{length(map,2)-1, 0});
        map[length(map,1)-1][0] = MONTAGNE;
        map[length(map,1)-2][length(map,2)-1] = MONTAGNE;
        println();
        kaomijiOrateurln("Déplace-toi jusqu'à ce que tu atteignes le drapeau rouge !");
        kaomijiOrateurln("Pour t'entraîner, appuie sur la touche [8] ou [6] ou [4] ou [2] ou [3] puis [ENTER] du clavier pour te déplacer!");
        afficherMap(map, ludophile);

        while(objectifPasAtteint(ludophile, map, but)){
            print("\n[8]:⬆️   ; [6]:➡️   ; [4]:⬅️   ; [2]:⬇️   ; [3]:🔄️\nChoix : ");
            int saisie = verificationString(readString());
            if(saisie == 8 || saisie == 6 || saisie == 4 || saisie == 2 || saisie == 3){
                if(saisie == 3){
                    idxBoucle = saisieWhile(choix, idxBoucle);
                    boucleWhile(ludophile, map, but, choix, idxBoucle);
                    idxBoucle++;
                } else {
                    deplacement(saisie, ludophile, map, but, 0, idxBoucle, choix);
                }
            } else {
                kaomijiOrateurln(JAUNE + "Tu ne t'es pas déplacé. Assure-toi d'appuyer sur le bon bouton pour te déplacer !" + RESET);
            }
        }
        delayln(1000);
        kaomijiOrateurln(VERT + "Félicitations ! On va maintenant t'évaluer à travers une épreuve ! (Pas de panique, tu ne peux pas mourir durant le tutoriel.)\n" + RESET);
    }

    //Challenge 3 (Boucle While)
    void challengeTutorielWhile(Joueur ludophile, Objectif but){
        String[][] map = new String[10][10];
        Selection choix = newSelection();
        int idxBoucle = 0;
        for(int cpt=0; cpt<3; cpt++){
            kaomijiOrateurln(BLEU + "C'est parti pour le défi numéro " + (cpt+1) + "/3 !" + RESET);
            initialisationMap(map, ludophile, but);
            afficherMap(map, ludophile);
            while(objectifPasAtteint(ludophile, map, but)){
                print("\n[8]:⬆️   ; [6]:➡️   ; [4]:⬅️   ; [2]:⬇️   ; [3]:🔄️\nChoix : ");
                int saisie = verificationString(readString());
                if(saisie == 8 || saisie == 6 || saisie == 4 || saisie == 2 || saisie == 3){
                    if(saisie == 3){
                        idxBoucle = saisieWhile(choix, idxBoucle);
                        boucleWhile(ludophile, map, but, choix, idxBoucle);
                        idxBoucle++;
                    } else {
                        deplacement(saisie, ludophile, map, but, 0, idxBoucle, choix);
                    }
                } else {
                    kaomijiOrateurln(JAUNE + "Tu ne t'es pas déplacé. Assure-toi d'appuyer sur le bon bouton pour te déplacer !" + RESET);
                }
            }
            println();
        }
        kaomijiOrateurln(VERT + "Bravo, champion(ne) ! Tu as réussi ce challenge en perdant l'équivalent de " + (10-nbVie) +" PV !" + RESET);
        nbVie = 10;
    }

    //Algorithme tutoriel
    void tutorielAlgo(Joueur ludophile, Objectif but, String[][] map){
        mapTutoriel(ludophile, but, map, new int[]{0, length(map,1)-1}, new int[]{0, length(map,2)-1});
        kaomijiOrateurln("Déplace-toi en créant des instructions séquentielles jusqu'à ce que tu atteignes le drapeau rouge !");
        kaomijiOrateurln("Utilise les commandes disponibles comme (haut, bas, etc.) et n'oublie pas de vérifier tes étapes pour t'assurer que tu es sur la bonne voie.");
        afficherMap(map, ludophile);
        delay(1000);
        lecteurFichier("ressources/File/explicationAlgo.txt");
        while(objectifPasAtteint(ludophile, map, but)){   
            executionAlgorithme(ludophile, map, but);
        }
        delayln(1000);
        kaomijiOrateurln(VERT + "Félicitations ! Maintenant, mets tout ce que tu as appris à travers cette dernière épreuve." + RESET);
    }

    //Challenge 4 (Algorithme)
    void challengeTutorielAlgorithme(Joueur ludophile, Objectif but){
        String[][] map = new String[10][10];
        Selection choix = newSelection();
        for(int cpt=0; cpt<3; cpt++){
            kaomijiOrateurln(BLEU + "C'est parti pour le défi numéro " + (cpt+1) + "/3 !" + RESET);
            initialisationMap(map, ludophile, but);
            afficherMap(map, ludophile);
            delay(1000);
            lecteurFichier("ressources/File/explicationAlgo.txt");
            while(objectifPasAtteint(ludophile, map, but)){
                executionAlgorithme(ludophile, map, but);
            }
            println();
        }
        kaomijiOrateurln(VERT + "Bravo, champion(ne) ! Tu as réussi ce challenge en perdant l'équivalent de " + (10-nbVie) +" PV !" + RESET);
        nbVie = 10;
    }

    /* ======================================================= */
    /* Tout ce qui conditon booléenne pour les boucles while() */
    /* ======================================================= */
    
    //Condition : Vers le nord, c'est un CHEMIN?
    boolean estCheminNord(Joueur ludophile, String[][] map){
        return ludophile.idxL != 0 && equals(map[ludophile.idxL-1][ludophile.idxC], CHEMIN);
    }

    void testEstCheminNord(){
        Joueur ludophile = newJoueur();
        String[][] map;
        ludophile.idxC = 1;

        ludophile.idxL = 1;
        map = new String[][]{{CHEMIN, CHEMIN, CHEMIN},
                             {CHEMIN, ludophile.personnage, CHEMIN},
                             {CHEMIN, CHEMIN, CHEMIN}};
        assertTrue(estCheminNord(ludophile, map));

        map = new String[][]{{CHEMIN, BOMBE, CHEMIN},
                             {CHEMIN, ludophile.personnage, CHEMIN},
                             {CHEMIN, CHEMIN, CHEMIN}};
        assertFalse(estCheminNord(ludophile, map));

        ludophile.idxL = 0;
        map = new String[][]{{CHEMIN, ludophile.personnage, CHEMIN},
                             {CHEMIN, CHEMIN, CHEMIN},
                             {CHEMIN, CHEMIN, CHEMIN}};
        assertFalse(estCheminNord(ludophile, map));
    }

    //Condition : Vers le sud, c'est un CHEMIN?
    boolean estCheminSud(Joueur ludophile, String[][] map){
        return ludophile.idxL != length(map, 1)-1 && equals(map[ludophile.idxL+1][ludophile.idxC], CHEMIN);
    }

    void testEstCheminSud(){
        Joueur ludophile = newJoueur();
        String[][] map;
        ludophile.idxC = 1;

        ludophile.idxL = 1;
        map = new String[][]{{CHEMIN, CHEMIN, CHEMIN},
                             {CHEMIN, ludophile.personnage, CHEMIN},
                             {CHEMIN, CHEMIN, CHEMIN}};
        assertTrue(estCheminSud(ludophile, map));

        map = new String[][]{{CHEMIN, CHEMIN, CHEMIN},
                             {CHEMIN, ludophile.personnage, CHEMIN},
                             {CHEMIN, ARBRE, CHEMIN}};
        assertFalse(estCheminSud(ludophile, map));

        ludophile.idxL = 2;
        map = new String[][]{{CHEMIN, CHEMIN, CHEMIN},
                             {CHEMIN, CHEMIN, CHEMIN},
                             {CHEMIN, ludophile.personnage, CHEMIN}};
        assertFalse(estCheminSud(ludophile, map));
    }

    //Condition : Vers l'est, c'est un CHEMIN?
    boolean estCheminEst(Joueur ludophile, String[][] map){
        return ludophile.idxC != length(map, 2)-1 && equals(map[ludophile.idxL][ludophile.idxC+1], CHEMIN);
    }

    void testEstCheminEst(){
        Joueur ludophile = newJoueur();
        String[][] map;
        ludophile.idxL = 1;

        ludophile.idxC = 1;
        map = new String[][]{{CHEMIN, CHEMIN, CHEMIN},
                             {CHEMIN, ludophile.personnage, CHEMIN},
                             {CHEMIN, CHEMIN, CHEMIN}};
        assertTrue(estCheminEst(ludophile, map));

        map = new String[][]{{CHEMIN, CHEMIN, CHEMIN},
                             {CHEMIN, ludophile.personnage, LAVE},
                             {CHEMIN, CHEMIN, CHEMIN}};
        assertFalse(estCheminEst(ludophile, map));

        ludophile.idxC = 2;
        map = new String[][]{{CHEMIN, CHEMIN, CHEMIN},
                             {CHEMIN, CHEMIN, ludophile.personnage},
                             {CHEMIN, CHEMIN, CHEMIN}};
        assertFalse(estCheminEst(ludophile, map));
    }

    //Condition : Vers l'ouest, c'est un CHEMIN?
    boolean estCheminOuest(Joueur ludophile, String[][] map){
        return ludophile.idxC != 0 && equals(map[ludophile.idxL][ludophile.idxC-1], CHEMIN);
    }

    void testEstCheminOuest(){
        Joueur ludophile = newJoueur();
        String[][] map;
        ludophile.idxL = 1;
        
        ludophile.idxC = 1;
        map = new String[][]{{CHEMIN, CHEMIN, CHEMIN},
                             {CHEMIN, ludophile.personnage, CHEMIN},
                             {CHEMIN, CHEMIN, CHEMIN}};
        assertTrue(estCheminOuest(ludophile, map));

        map = new String[][]{{CHEMIN, CHEMIN, CHEMIN},
                             {MONTAGNE, ludophile.personnage, CHEMIN},
                             {CHEMIN, CHEMIN, CHEMIN}};
        assertFalse(estCheminOuest(ludophile, map));

        ludophile.idxC = 0;
        map = new String[][]{{CHEMIN, CHEMIN, CHEMIN},
                             {ludophile.personnage, CHEMIN, CHEMIN},
                             {CHEMIN, CHEMIN, CHEMIN}};
        assertFalse(estCheminOuest(ludophile, map));
    }

    /* ============================================================= */
    /* Choix de déplacement globales, boucle for() et boucle while() */
    /* ============================================================= */

    //Choix de déplacement global
    void deplacement(int choix, Joueur ludophile, String[][] map, Objectif but, int idxPour, int idxWhile, Selection select){
        if(choix == 8){
            avancerNord(ludophile, map, but);
        } else if(choix == 6){
            avancerEst(ludophile, map, but);
        } else if(choix == 4){
            avancerOuest(ludophile, map, but);
        } else if(choix == 2){
            avancerSud(ludophile, map, but);
        } else if(choix == 1){
            boucleCompteur(ludophile, map, but, idxPour, select);
        } else if(choix == 3) {
            boucleWhile(ludophile, map, but, select, idxWhile);
        }
    }

    //Saisie pour boucle for()
    int saisiePour(Selection choix, int idx){
        if(idx<length(choix.nbChoix)){
            kaomijiOrateur("Dans quelle direction aimerais-tu aller ?\n" + espacement(kaomijiPhrase("")) + "[8]:⬆️   ; [6]:➡️   ; [4]:⬅️   ; [2]:⬇️\nChoix : ");
            choix.nbChoix[idx] = verificationString(readString());
            while(choix.nbChoix[idx] == 1 || choix.nbChoix[idx] == 3 || choix.nbChoix[idx] == 5 || choix.nbChoix[idx] == 7 || choix.nbChoix[idx] >= 9){
                kaomijiOrateur(JAUNE + "Votre saisie ne correspond à aucun déplacement, veuillez réessayer: " + RESET);
                choix.nbChoix[idx] = verificationString(readString());
            }
            delay(500);
            kaomijiOrateur("Combien de fois veux-tu aller dans cette direction ? ");
            choix.nbCases[idx] = verificationString(readString());
            return idx;
        } else {
            for(int cpt=0; cpt<length(choix.nbChoix); cpt++){
                choix.nbCases[cpt] = 0;
                choix.nbChoix[cpt] = 0;
            }
            idx = 0;
            return saisiePour(choix, idx);
        }
    }

    //
    void deplacementPour(Joueur ludophile, String[][] map, Objectif but, Selection choix, int idx){
        boolean deplacementImpossible = false;
        for(int cpt = 0; cpt<choix.nbCases[idx]; cpt++){
            if(choix.nbChoix[idx] == 8){
                deplacementImpossible = deplacementImpossibleNord(ludophile, map);
            } else if(choix.nbChoix[idx] == 6){
                deplacementImpossible = deplacementImpossibleEst(ludophile, map);
            } else if(choix.nbChoix[idx] == 4){
                deplacementImpossible = deplacementImpossibleOuest(ludophile, map);
            } else if(choix.nbChoix[idx] == 2){
                deplacementImpossible = deplacementImpossibleSud(ludophile, map);
            }

            if(deplacementImpossible == true || nbVie<1){
                cpt = choix.nbCases[idx];
                if(deplacementImpossible == true){
                    println();
                    kaomijiOrateurln(ROUGE + "Ce déplacement n'est pas possible. N'oublie pas, l'ordinateur fait juste ce que tu lui dis de faire, même si ça n'a pas l'air correcte !" + RESET);
                    erreurAlgorithme(ludophile, map, but);
                }
            } else {
                deplacement(choix.nbChoix[idx], ludophile, map, but, idx, 0, choix);
                delay(500);
            }
        }
    }

    //Saisie pour boucle while()
    int saisieWhile(Selection choix, int idx){
        if(idx<length(choix.direction)){
            kaomijiOrateur("Dans quelle direction aimerais-tu aller ?\n" + espacement(kaomijiPhrase("")) + "[8]:⬆️   ; [6]:➡️   ; [4]:⬅️   ; [2]:⬇️\nChoix : ");
            choix.direction[idx] = verificationString(readString());
            while(choix.direction[idx] == 1 || choix.direction[idx] == 3 || choix.direction[idx] == 5 || choix.direction[idx] == 7 || choix.direction[idx] >= 9){
                kaomijiOrateur(JAUNE + "Votre saisie ne correspond à aucun déplacement, veuillez réessayer: " + RESET);
                choix.direction[idx] = verificationString(readString());
            }
            delay(500);
            String condition = condition(choix, idx);
            return idx;
        } else {
            for(int cpt=0; cpt<length(choix.direction); cpt++){
                choix.direction[cpt] = 0;
                choix.conditions[cpt] = "";
            }
            idx = 0;
            return saisieWhile(choix, idx);
        }
    }

    //Vérification de condition pour boucle while()
    String condition(Selection choix, int idx){
        String[] directionListe = new String[]{"⬆️", "➡️", "⬅️", "⬇️"};
        String direction = "";

        if(choix.direction[idx] == 8){
            direction = directionListe[0];
        } else if(choix.direction[idx] == 6){
            direction = directionListe[1];
        } else if(choix.direction[idx] == 4){
            direction = directionListe[2];
        } else if(choix.direction[idx] == 2){
            direction = directionListe[3];
        }

        kaomijiOrateurln("Tu veux aller vers " + direction + "   Tant Que :");
        print(espacement(kaomijiPhrase("")) + "[a] : La case est un " + CHEMIN + "\n" + espacement(kaomijiPhrase("")) + "[*] : D'autres conditions seront ajoutées bientôt !\n" + espacement(kaomijiPhrase("")) + "Choix : ");
        choix.conditions[idx] = toLowerCase(readString());
        while(!equals(choix.conditions[idx], "a")){
            kaomijiOrateur(JAUNE + "Tu n'as pas choisi une lettre qui correspond à l'une des conditions ci-dessus, essaie encore : " + RESET);
            choix.conditions[idx] = readString();
        }
        return choix.conditions[idx];
    }

    //
    void deplacementWhile(Joueur ludophile, String[][] map, Objectif but, Selection choix, int idx){
        boolean estChemin = false;
        if(equals(choix.conditions[idx], "a")){
            if(choix.direction[idx] == 8){
                estChemin = estCheminNord(ludophile, map);
            } else if(choix.direction[idx] == 6){
                estChemin = estCheminEst(ludophile, map);
            } else if(choix.direction[idx] == 4){
                estChemin = estCheminOuest(ludophile, map);
            } else if(choix.direction[idx] == 2){
                estChemin = estCheminSud(ludophile, map);
            }

            if(estChemin == false){
                kaomijiOrateurln(JAUNE + "Ce déplacement n'est pas possible. N'oublie pas, l'ordinateur fait juste ce que tu lui dis de faire, même si ça n'a pas l'air correcte !" + RESET);
                erreurAlgorithme(ludophile, map, but);
            } else {
                while(estChemin == true){
                    deplacement(choix.direction[idx], ludophile, map, but, 0, idx, choix);
                    if(choix.direction[idx] == 8){
                        estChemin = estCheminNord(ludophile, map);
                    } else if(choix.direction[idx] == 6){
                        estChemin = estCheminEst(ludophile, map);
                    } else if(choix.direction[idx] == 4){
                        estChemin = estCheminOuest(ludophile, map);
                    } else if(choix.direction[idx] == 2){
                        estChemin = estCheminSud(ludophile, map);
                    }
                    delay(500);
                }
            }   
        }
    }

    /* ==================================================== */
    /* Erreur lors de l'éxécution de l'algorithme du joueur */
    /* ==================================================== */

    //Si dans l'algorithme du joueur, il y a un erreur de déplacement, il recommence
    void erreurAlgorithme(Joueur ludophile, String[][] map, Objectif but){
        if(ludophile.tutoriel == false){
            println();
            kaomijiOrateurln(JAUNE + "Ton algorithme a une erreur, tu dois recommencer." + RESET);
            map[ludophile.idxL][ludophile.idxC] = CHEMIN;
            placementJoueur(map, ludophile);
            map[but.idxL][but.idxC] = but.DRAPEAU;
            delay(1500);
            afficherMap(map, ludophile);
            kaomijiOrateurln("C'est ce qui arrive quand on fait des erreurs dans un programme.");
            kaomijiOrateurln(VERT + "N'hésite pas à le revoir et à réessayer ! Recommençons !\n" + RESET);
            executionAlgorithme(ludophile, map, but);
        }
    }

    /* ============================================ */
    /* Tout ce qui est relatif au données du joueur */
    /* ============================================ */

    //Récupération des données des joueur déjà existant
    String[][] data(String fileCSV){
        CSVFile dataLudophile = loadCSV(fileCSV);
        String[][] dataCSV = new String[rowCount(dataLudophile)+1][columnCount(dataLudophile)];

        for(int idxI=0; idxI<length(dataCSV, 1)-1; idxI++){
            for(int idxJ=0; idxJ<length(dataCSV, 2); idxJ++){
                dataCSV[idxI][idxJ] = getCell(dataLudophile, idxI, idxJ);
            }
        }

        return dataCSV;
    }

    //Enregistrement de donnée ou modification
    void saveData(Joueur ludophile, String fileCSV){
        String[][] data = data(fileCSV);
        ludophile.id = stringtoInt(data[length(data,1)-2][0]);

        data[length(data)-1][0] = "" + (ludophile.id + 1);
        data[length(data)-1][1] = ludophile.nom;
        data[length(data)-1][2] = ludophile.genre;
        data[length(data)-1][3] = "" + ludophile.tutoriel;
        data[length(data)-1][4] = "" + nbVie;
        data[length(data)-1][5] = "" + ludophile.nbReussite;
        data[length(data)-1][6] = "" + ludophile.nbBouclier;
        data[length(data)-1][7] = "" + ludophile.immunite;
        data[length(data)-1][8] = ludophile.mdp;

        saveCSV(data, fileCSV);
    }

    //Retournera un tableau avec les noms de tous les joueurs
    String[] nomLudophile(String fileCSV){
        String[][] data = data(fileCSV);
        String[] nom = new String[length(data, 1)-2];

        for(int idx=0; idx<length(nom); idx++){
            nom[idx] = data[idx+1][1];
        }

        return nom;
    }

    //Retournera un tableau avec les mdp de tous les joueurs
    String[] mdpLudophile(String fileCSV){
        String[][] data = data(fileCSV);
        String[] mdp = new String[length(data, 1)-2];

        for(int idx=0; idx<length(mdp); idx++){
            mdp[idx] = data[idx+1][8];
        }

        return mdp;
    }

    //Retournera la taille du tableau qui correspondra à l'ID du joueur
    int idLudophile(String fileCSV){
        String[][] data = data(fileCSV);
        int lengthData = 0;

        for(int idx=0; idx<length(data, 1)-2; idx++){
            lengthData++;
        }

        return lengthData-1;
    }

    //Restauration des données
    void restoreData(Joueur ludophile, String fileCSV, int idx){
        String[][] data = data(fileCSV);
        ludophile.id = stringtoInt(data[idx+1][0]);
        ludophile.nom = data[idx+1][1];
        ludophile.genre = data[idx+1][2];
        ludophile.tutoriel = stringToBoolean(data[idx+1][3]);
        nbVie = stringtoInt(data[idx+1][4]);
        ludophile.nbReussite = stringtoInt(data[idx+1][5]);
        ludophile.nbBouclier = stringtoInt(data[idx+1][6]);
        ludophile.immunite = stringToBoolean(data[idx+1][7]);
    }

    //Si le joueur est existant et connais son mdp, il récupère ses données
    void restoreLudophile(Joueur ludophile, String fileCSV){
        String[] nom = nomLudophile(fileCSV);
        String[] mdp = mdpLudophile(fileCSV);
        String choix;
        int idx = 0;

        while(idx<length(nom) && !equals(nom[idx], ludophile.nom)){
            idx++;
        }

        if(equals(nom[idx], ludophile.nom)){
            kaomijiOrateur(VERT + "Ding Dong ! On a trouvé ton nom dans nos données, c’est bien toi ? [O : Oui ; N : Non] : " + RESET);
            choix = toUpperCase(readString());
            while((!equals(choix, "O") && !equals(choix, "OUI")) && (!equals(choix, "N") && !equals(choix, "NON"))){
                kaomijiOrateur(JAUNE + "Vérifie ce que tu as écrit : tu dois choisir entre Oui (O) ou Non (N) : " + RESET);
                choix = toUpperCase(readString());
            }

            if(equals(choix, "O") || equals(choix, "OUI")){
                kaomijiOrateur("Entrez votre mot secret : ");
                choix = readString();
                while(!equals(choix, mdp[idx]) && !equals(choix, "0")){
                    kaomijiOrateur(JAUNE + "Mot secret incorrect, essaie encore ou appuie sur [0] pour annuler : " + RESET);
                    choix = readString();
                }
            } else {
                ludophileSetUp(ludophile, fileCSV);
            }

            if(equals(choix, mdp[idx])){
                kaomijiOrateurln(JAUNE + "Restauration de vos données en cours..." + RESET);
                restoreData(ludophile, fileCSV, idx);
                kaomijiOrateurln(VERT + "Données restaurées, re-bonjour " + ludophile.nom + RESET);
            } else if(equals(choix, "0") ){
                ludophileSetUp(ludophile, fileCSV);
            }
        }
    }

    //Création personnage si la saisie était un personnage existant
    void ludophileSetUp(Joueur ludophile, String fileCSV){
        ludophile.nom = nomUnique(ludophile, fileCSV);
        estNameless(ludophile);
        delay(1000);
        genreJoueur(ludophile);
        if(!equals(ludophile.nom, "Nameless")){
            kaomijiOrateur("Afin de pouvoir revenir là où tu t'es arrêté, je te demande d'entrer un mot que seul toi tu connais : ");
            ludophile.mdp = readString();
            kaomijiOrateurln(JAUNE + "Enregistrement de tes données en cours..." + RESET);
            saveData(ludophile, fileCSV);
            kaomijiOrateurln(VERT + "Enregistrement terminé !" + RESET);
            ludophile.id = idLudophile(csvData);
        }
    }

    //Afin de modifier les données du joueur
    void adjustLudophile(Joueur ludophile, String fileCSV){
        String[][] data = data(fileCSV);
        String[][] adjustData = new String[length(data, 1)-1][length(data, 2)];
        
        for(int idxL=0; idxL<length(adjustData, 1); idxL++){
            for(int idxC=0; idxC<length(adjustData, 2); idxC++){
                adjustData[idxL][idxC] = data[idxL][idxC];
            }
        }

        kaomijiOrateurln(JAUNE + "Sauvegarde de ta progression en cours..." + RESET);
        adjustData[ludophile.id+1][1] = ludophile.nom;
        adjustData[ludophile.id+1][2] = ludophile.genre;
        adjustData[ludophile.id+1][3] = "" + ludophile.tutoriel;
        adjustData[ludophile.id+1][4] = "" + nbVie;
        adjustData[ludophile.id+1][5] = "" + ludophile.nbReussite;
        adjustData[ludophile.id+1][6] = "" + ludophile.nbBouclier;
        adjustData[ludophile.id+1][7] = "" + ludophile.immunite;
        kaomijiOrateurln(VERT + "Sauvegarde Terminée !" + RESET);

        saveCSV(adjustData, fileCSV);
    }

    //Eviter les doublons de noms
    String nomUnique(Joueur ludophile, String fileCSV){
        String[] nom = nomLudophile(fileCSV);
        int idx = 0;
        String saisie;
        
        kaomijiOrateur("Entrez un nouveau nom : ");
        saisie = verificationNom(readString());
        while(idx<length(nom) && !equals(nom[idx], saisie)){
            idx++;
        }

        while(idx<length(nom) && equals(nom[idx], saisie)){
            idx = 0;
            kaomijiOrateur(JAUNE + "Ce nom existe déjà, choisis-en un autre : " + RESET);
            saisie = verificationNom(readString());
            while(idx<length(nom) && !equals(nom[idx], saisie)){
                idx++;
            }
        }

        return saisie;
    }

    //Retournera vrai si le nom saisie existe déjà
    boolean estNomExistant(Joueur ludophile, String fileCSV){
        String[] nom = nomLudophile(fileCSV);
        int idx = 0;

        while(idx<length(nom) && !equals(nom[idx], ludophile.nom)){
            idx++;
        }

        if(idx == length(nom)){
            return false;
        }

        return equals(nom[idx], ludophile.nom);
    }

    /* ================================================ */
    /* Tout ce qui est relatif à la fin d'un partie/jeu */
    /* ================================================ */
    //Si la saisie est != de 'fini', le joueur pousuivra le jeu
    String poursuivreJeu(Joueur ludophile){
        String saisie = "FINI";
        if(nbVie>0){
            ludophile.nbReussite++;
            kaomijiOrateurln(VERT + "Félicitation, vous avez terminé le niveau " + ludophile.nbReussite + " !" + RESET);
            if(!equals(ludophile.nom, "Nameless")){
                adjustLudophile(ludophile, csvData);
            }
            kaomijiOrateur("Tu veux continuer ? Appui sur [ENTER] pour continuer ou écrit 'Fini' pour t'arrêter : ");
            saisie = toUpperCase(readString());
            while(!equals(saisie, "") && !equals(saisie, "FINI")){
                kaomijiOrateur("Appui sur [ENTER] pour continuer ou écrit 'Fini' pour t'arrêter : ");
                saisie = toUpperCase(readString());
            }
        }
        return saisie;
    }
}
