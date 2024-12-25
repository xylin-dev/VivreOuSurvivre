class VivreOuSurvivre extends Program{
    
    /* ============================== */
    /* VARIABLE ET CONSTANTE GLOBALES  */
    /* ============================== */

    //Elément de la map
    final String CHEMIN = "⬛";
    final String ARBRE = "🌴";
    final String COCO = "🥥";
    final String MONTAGNE = "🗻";
    final String BOMBE = "💣";
    final String EXPLOSION = "💥";
    final String LAVE = "🌋";
    final String BRULE = "🔥";
    final String CARTE = "🎴";

    //Couleurs du texte selon de leurs fonctions
    final String VERT = "\u001B[32m"; //Réussite, objectif atteint.
    final String ROUGE = "\u001B[31m"; //Échec, erreur.
    final String BLEU = "\u001B[34m"; //Important, à ne pas négliger.
    final String JAUNE = "\u001B[33m"; //Alerte, attention nécessaire.
    String RESET = "\u001B[0m"; //Couleurs et Styles par défauts

    //Style du texte selon leurs fonctions
    final String GRAS = "\033[1m"; 

    //Nb de Vie du Joueur ainsi que son Nb de Vie Precedent pour Kaomiji
    int nbVie = 10;
    int nbViePrecedent = 10;

    //Nb de Reussite (>5 = Apparition du Troll)
    int nbReussite = 0;

    //Coordonné pour effet des éléments de la map
    int[] idxBombe = new int[8];
    int[] idxArbre = new int[8];
    int[] idxLave = new int[8];
    int[] idxCarte = new int[8];


    void algorithm(){
        Joueur ludophile = newJoueur();
        Objectif but = newObjectif();
        String jeu = "";

        String[][] map = new String[20][20];

        begin(ludophile, but);

        while(!equals(jeu, "Fini") || nbVie>0){
            initialisationMap(map, ludophile, but);
            afficherMap(map, ludophile);
            println();
            while(objectifAtteint(ludophile, map, but)){
                deplacement(ludophile, map);
            }
            kaomijiOrateur("Tu veux continuer ? Appui sur [ENTER] pour continuer ou écrit (Fini) pour t'arrêter : ");
            jeu = readString();
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
        return ludophile;
    }

    //Création de type : Objectif
    Objectif newObjectif(){
        Objectif but = new Objectif();
        return but;
    }



    /* ======================= */
    /* Kaomiji : Maître du Jeu */
    /* ======================= */
    
    //Trouve le minimum entre deux nombre
    int min(int premierNb, int deuxiemeNb){
        int nbMin;

        if(premierNb>deuxiemeNb){
            nbMin = deuxiemeNb;
        }else{
            nbMin = premierNb;
        }

        return nbMin;
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

    //Espacement dans le texte (principalement pour Kaomiji)
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
    void elementMap(String[][] map, String[] tab, double probabilite){
        double probabiliteAleatoire = random();
        int idxAleatoire = (int) (random()*length(tab));
        int idxLigneAleatoire = (int)(random()*length(map, 1));
        int idxColonneAleatoire = (int)(random()*length(map, 2));

        if(probabiliteAleatoire>probabilite){
            map[idxLigneAleatoire][idxColonneAleatoire] = tab[idxAleatoire];
        }
    }

    //Placement aléatoire de l'objectif du joueur dans la moitié de la map superieur
    void objectifMap(String[][] map, Objectif but){
        but.idxObjectifLigne = (int)(random()*length(map, 1)/2)+1;
        but.idxObjectifColonne = (int)(random()*length(map, 2)/2)+1;

        map[but.idxObjectifLigne][but.idxObjectifColonne] = but.DRAPEAU;
    }

    //Placement du joueur dans la map
    void placementJoueur(String[][] map, Joueur ludophile){
        ludophile.idxL = length(map, 1)-1;
        ludophile.idxC = length(map, 2)-1;
        map[ludophile.idxL][ludophile.idxC] = ludophile.personnage;
    }
    
    //Initialisera la carte
    void initialisationMap(String[][] map, Joueur ludophile, Objectif but){
        String[] element = new String[]{ARBRE, MONTAGNE, BOMBE, LAVE, CARTE};
        double probabilite = 0.3;

        remplissageMap(map);

        for(int cpt=0; cpt<(length(map, 1)*length(map,2))/3; cpt++){
            elementMap(map, element, probabilite);
        }

        objectifMap(map, but);
        placementJoueur(map, ludophile);
        map[0][0] = MONTAGNE;
    }

    //Affichera la carte
    void afficherMap(String[][] map, Joueur ludophile){
        println();
        for(int idxI=0; idxI<length(map,1); idxI++){
            for(int idxJ=0; idxJ<length(map,2); idxJ++){
                print(map[idxI][idxJ]);
            }
            println();
        } 
        informationJoueur(ludophile, map);
        println();
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
        return true;
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



    /* =============================== */
    /* Déplacement et boucle du Joueur */
    /* =============================== */

    //Vérification du déplacement vers le Nord
    boolean deplacementPossibleNord(Joueur ludophile, String[][] map){
        if(ludophile.idxL == 0){
            return false;
        }

        if(equals(map[ludophile.idxL-1][ludophile.idxC], MONTAGNE)){
            return false;
        }

        return true;
    }

    void testDeplacementPossibleNord(){
        Joueur ludophile = newJoueur();
        ludophile.personnage = "👨";

        ludophile.idxL = 0;
        ludophile.idxC = 1;
        String[][] map = new String[][]{{CHEMIN,ludophile.personnage,CHEMIN},
                                        {CHEMIN,CHEMIN,CHEMIN},
                                        {CHEMIN,CHEMIN,CHEMIN}};
        assertFalse(deplacementPossibleNord(ludophile, map));

        ludophile.idxL = 1;
        ludophile.idxC = 1;
        map = new String[][]{{CHEMIN,MONTAGNE,CHEMIN},
                             {CHEMIN,ludophile.personnage,CHEMIN},
                             {CHEMIN,CHEMIN,CHEMIN}};
        assertFalse(deplacementPossibleNord(ludophile, map));
        
        ludophile.idxL = 1;
        ludophile.idxC = 1;
        map = new String[][]{{CHEMIN,CHEMIN,CHEMIN},
                             {CHEMIN,ludophile.personnage,CHEMIN},
                             {CHEMIN,CHEMIN,CHEMIN}};
        assertTrue(deplacementPossibleNord(ludophile, map));
    }

    //Vérification du déplacement vers le Sud
    boolean deplacementPossibleSud(Joueur ludophile, String[][] map){
        if(ludophile.idxL == (length(map, 1)-1)){
            return false;
        }

        if(equals(map[ludophile.idxL+1][ludophile.idxC], MONTAGNE)){
            return false;
        }

        return true;
    }

    void testDeplacementPossibleSud(){
        Joueur ludophile = newJoueur();
        ludophile.personnage = "👨";

        ludophile.idxL = 2;
        ludophile.idxC = 1;
        String[][] map = new String[][]{{CHEMIN,CHEMIN,CHEMIN},
                                        {CHEMIN,CHEMIN,CHEMIN},
                                        {CHEMIN,ludophile.personnage,CHEMIN}};
        assertFalse(deplacementPossibleSud(ludophile, map));

        ludophile.idxL = 1;
        ludophile.idxC = 1;
        map = new String[][]{{CHEMIN,CHEMIN,CHEMIN},
                             {CHEMIN,ludophile.personnage,CHEMIN},
                             {CHEMIN,MONTAGNE,CHEMIN}};
        assertFalse(deplacementPossibleSud(ludophile, map));
        
        ludophile.idxL = 1;
        ludophile.idxC = 1;
        map = new String[][]{{CHEMIN,CHEMIN,CHEMIN},
                             {CHEMIN,ludophile.personnage,CHEMIN},
                             {CHEMIN,CHEMIN,CHEMIN}};
        assertTrue(deplacementPossibleSud(ludophile, map));
    }

    //Vérification du déplacement vers l'Ouest
    boolean deplacementPossibleOuest(Joueur ludophile, String[][] map){
        if(ludophile.idxC == 0){
            return false;
        }

        if(equals(map[ludophile.idxL][ludophile.idxC-1], MONTAGNE)){
            return false;
        }

        return true;
    }

    void testDeplacementPossibleOuest(){
        Joueur ludophile = newJoueur();
        ludophile.personnage = "👨";

        ludophile.idxL = 1;
        ludophile.idxC = 0;
        String[][] map = new String[][]{{CHEMIN,CHEMIN,CHEMIN},
                                        {ludophile.personnage,CHEMIN,CHEMIN},
                                        {CHEMIN,CHEMIN,CHEMIN}};
        assertFalse(deplacementPossibleOuest(ludophile, map));

        ludophile.idxL = 1;
        ludophile.idxC = 1;
        map = new String[][]{{CHEMIN,CHEMIN,CHEMIN},
                             {MONTAGNE,ludophile.personnage,CHEMIN},
                             {CHEMIN,CHEMIN,CHEMIN}};
        assertFalse(deplacementPossibleOuest(ludophile, map));
        
        ludophile.idxL = 1;
        ludophile.idxC = 1;
        map = new String[][]{{CHEMIN,CHEMIN,CHEMIN},
                             {CHEMIN,ludophile.personnage,CHEMIN},
                             {CHEMIN,CHEMIN,CHEMIN}};
        assertTrue(deplacementPossibleOuest(ludophile, map));
    }

    //Vérification du déplacement vers l'Est
    boolean deplacementPossibleEst(Joueur ludophile, String[][] map){
        if(ludophile.idxC == (length(map, 2)-1)){
            return false;
        }

        if(equals(map[ludophile.idxL][ludophile.idxC+1], MONTAGNE)){
            return false;
        }

        return true;
    }

    void testDeplacementPossibleEst(){
        Joueur ludophile = newJoueur();
        ludophile.personnage = "👨";

        ludophile.idxL = 1;
        ludophile.idxC = 2;
        String[][] map = new String[][]{{CHEMIN,CHEMIN,CHEMIN},
                                        {CHEMIN,CHEMIN,ludophile.personnage},
                                        {CHEMIN,CHEMIN,CHEMIN}};
        assertFalse(deplacementPossibleEst(ludophile, map));

        ludophile.idxL = 1;
        ludophile.idxC = 1;
        map = new String[][]{{CHEMIN,CHEMIN,CHEMIN},
                             {CHEMIN,ludophile.personnage,MONTAGNE},
                             {CHEMIN,CHEMIN,CHEMIN}};
        assertFalse(deplacementPossibleEst(ludophile, map));
        
        ludophile.idxL = 1;
        ludophile.idxC = 1;
        map = new String[][]{{CHEMIN,CHEMIN,CHEMIN},
                             {CHEMIN,ludophile.personnage,CHEMIN},
                             {CHEMIN,CHEMIN,CHEMIN}};
        assertTrue(deplacementPossibleEst(ludophile, map));
    }

    //Avancer vers le Nord
    void avancerNord(Joueur ludophile, String[][] map){
        if(!deplacementPossibleNord(ludophile, map)){
            kaomijiOrateurln(ROUGE + "Ce déplacement n'est pas possible !" + RESET);
        } else {
            map[ludophile.idxL-1][ludophile.idxC] = ludophile.personnage;
            ludophile.idxL--;
            map[ludophile.idxL+1][ludophile.idxC] = CHEMIN;
            afficherMap(map, ludophile);
            println();
            effetBombe(ludophile, map);
            effetArbre(ludophile, map);
            effetLave(ludophile, map);
            effetCarte(ludophile, map);
        } 
    }

    //Avancer vers le Sud
    void avancerSud(Joueur ludophile, String[][] map){
        if(!deplacementPossibleSud(ludophile, map)){
            kaomijiOrateurln(ROUGE + "Ce déplacement n'est pas possible !" + RESET);
        } else {
            map[ludophile.idxL+1][ludophile.idxC] = ludophile.personnage;
            ludophile.idxL++;
            map[ludophile.idxL-1][ludophile.idxC] = CHEMIN;
            afficherMap(map, ludophile);
            println();
            effetBombe(ludophile, map);
            effetArbre(ludophile, map);
            effetLave(ludophile, map);
            effetCarte(ludophile, map);
        }
        
    }

    //Avancer vers l'Est
    void avancerEst(Joueur ludophile, String[][] map){
        if(!deplacementPossibleEst(ludophile, map)){
            kaomijiOrateurln(ROUGE + "Ce déplacement n'est pas possible !" + RESET);
        } else {
            map[ludophile.idxL][ludophile.idxC+1] = ludophile.personnage;
            ludophile.idxC++;
            map[ludophile.idxL][ludophile.idxC-1] = CHEMIN;
            afficherMap(map, ludophile);
            println();
            effetBombe(ludophile, map);
            effetArbre(ludophile, map);
            effetLave(ludophile, map);
            effetCarte(ludophile, map);
        }
    }

    //Avancer vers l'Ouest
    void avancerOuest(Joueur ludophile, String[][] map){
        if(!deplacementPossibleOuest(ludophile, map)){
            kaomijiOrateurln(ROUGE + "Ce déplacement n'est pas possible !" + RESET);
        } else {
            map[ludophile.idxL][ludophile.idxC-1] = ludophile.personnage;
            ludophile.idxC--;
            map[ludophile.idxL][ludophile.idxC+1] = CHEMIN;
            afficherMap(map, ludophile);
            println();
            effetBombe(ludophile, map);
            effetArbre(ludophile, map);
            effetLave(ludophile, map);
            effetCarte(ludophile, map);
        }
    }

    //Déplacement en boucle à compteur selon le choix du Joueur
    void boucleCompteur(Joueur ludophile, String[][] map){
        String saisie;
        int choix;
        int nbCases;

        kaomijiOrateur("Dans quelle direction aimerais-tu aller ?\n" + 
                        espacement(maitreKaomiji(nbVie) + " - ") + "(8):⬆️   ; (6):➡️   ; (4):⬅️   ; (2):⬇️   ; (0):🔙\nChoix : ");
        saisie = readString();
        choix = verificationString(saisie);
        delay(500);
        
        if(choix == 0){
            deplacement(ludophile, map);
        } else {
            kaomijiOrateur("Combien de fois veux-tu aller dans cette direction ? ");
            saisie = readString();
            nbCases = verificationString(saisie);

            choixDeplacementBoucle(choix, nbCases, ludophile, map);
        }
    }

    //Déplacement en boucle while() selon le choix du Joueur
    void boucleWhile(Joueur ludophile, String[][] map){
        String saisie;
        int choix;

        kaomijiOrateur("Dans quelle direction aimerais-tu aller ?\n" + 
                        espacement(maitreKaomiji(nbVie) + " - ") + "(8):⬆️   ; (6):➡️   ; (4):⬅️   ; (2):⬇️   ; (0):🔙\nChoix : ");
        saisie = readString();
        choix = verificationString(saisie);
        delay(500);
        
        if(choix == 0){
            deplacement(ludophile, map);
        } else {
            choixDeplacementWhile(choix, ludophile, map);
        }
    }



    /* ============================================ */
    /* Création et exécution d'algorithme du Joueur */
    /* ============================================ */
    



    /* ================================================================== */
    /* Tout ce qui est relatif à la création et information du personnage */
    /* ================================================================== */

    //Genre du Joueur
    String genreJoueur(Joueur ludophile){
        kaomijiOrateur("Quel est votre genre [Masculin (M); Feminin (F)] : ");
        ludophile.genre = readString();
        while((!equals(ludophile.genre, "Masculin") && !equals(ludophile.genre, "M")) && (!equals(ludophile.genre, "Feminin") && !equals(ludophile.genre, "F"))){
            kaomijiOrateur(ROUGE + "Non, vous devez choisir entre Masculin (ou M) et Feminin (ou F) : " + RESET);
            ludophile.genre = readString();
        }

        if(equals(ludophile.genre, "M")){
            ludophile.genre = "Masculin";
        } else if(equals(ludophile.genre, "F")){
            ludophile.genre = "Feminin";
        }

        return ludophile.genre;
    }

    //Personnage du Joueur
    String personnageJoueur(Joueur ludophile){
        String[] personnageMasculin = new String[]{"👨","👦","👶","🌞"};
        String[] personnageFeminin = new String[]{"👩","👧","👶","🌝"};

        if(equals(ludophile.genre, "Masculin")){
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
            println(espacement(maitreKaomiji(nbVie) + " - ") + (idx+1) + " : " + personnage[idx]);
            delay(500);
        }
    }

    //Selection de personnage
    int selectionPersonnage(String[] personnage){
        kaomijiOrateur("Choisis un personnage en tapant le numéro qui lui correspond : ");
        String saisie = readString();
        int choix = verificationString(saisie);
        while((choix>length(personnage) || choix<1)){
            kaomijiOrateur(ROUGE + "Ton choix n'est pas bon, essaie encore : " + RESET);
            saisie = readString();
            choix = verificationString(saisie);
        }

        return choix-1;
    }

    //Récaputilif de la création du personnage
    void recaputilatif(Joueur ludophile){
        kaomijiOrateurln("Voici un récapitulatif de ce que tu m'as donné : ");
        print(espacement(maitreKaomiji(nbVie) + " - ") + "Ton nom est : " + ludophile.nom + "\n");
        delay(500);
        print(espacement(maitreKaomiji(nbVie) + " - ") + "Ton genre est : " + ludophile.genre + "\n");
        delay(500);
        print(espacement(maitreKaomiji(nbVie) + " - ") + "Ton personnage est : " + ludophile.personnage + "\n");
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
        print(GRAS + ludophile.nom + " - PV: " + nbCoeur(nbVie) + " ; Coordonées: " + positionJoueur(ludophile, map) + " ; Bouclier: " + nbBouclier(ludophile) + " ; Immunité: " + immuniteToString(ludophile) + " ; Reussite: " + nbReussite + RESET);
    }



    /* ======================================================= */
    /* Tout ce qui est relatif au effet des éléments de la map */
    /* ======================================================= */

    //Retournera vrai si le Joueur a atteint l'objectif
    boolean objectifAtteint(Joueur ludophile, String[][] map, Objectif but){
        return equals(map[but.idxObjectifLigne][but.idxObjectifColonne], but.DRAPEAU);
    }

    void testObjectifAtteint(){
        Joueur ludophile = newJoueur();
        Objectif but = newObjectif();
        String[][] map;

        ludophile.personnage = "👨";

        but.idxObjectifLigne = 0;
        but.idxObjectifColonne = 1;

        map = new String[][]{{CHEMIN,but.DRAPEAU,CHEMIN},
                             {ludophile.personnage, CHEMIN, CHEMIN},
                             {CHEMIN,CHEMIN,CHEMIN}};
        assertTrue(objectifAtteint(ludophile, map, but));

        map = new String[][]{{CHEMIN,ludophile.personnage,CHEMIN},
                             {CHEMIN, CHEMIN, CHEMIN},
                             {CHEMIN,CHEMIN,CHEMIN}};
        assertFalse(objectifAtteint(ludophile, map, but));
    }

    //Effet de la Bombe
    void effetBombe(Joueur ludophile, String[][] map){
        //Si la bombe se trouve au-dessus du joueur
        if(equals(map[idxBombe[0]][idxBombe[1]], ludophile.personnage)){
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
            println();
            idxBombe[0] = 0;
            idxBombe[1] = 0;
        }

        if(ludophile.idxL>0 && equals(map[ludophile.idxL-1][ludophile.idxC], BOMBE)){
            idxBombe[0] = ludophile.idxL-1;
            idxBombe[1] = ludophile.idxC;
        }

        //Si la bombe se trouve en dessous du joueur
        if(equals(map[idxBombe[2]][idxBombe[3]], ludophile.personnage)){
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
            println();
            idxBombe[2] = 0;
            idxBombe[3] = 0;
        }

        if(ludophile.idxL<length(map)-1 && equals(map[ludophile.idxL+1][ludophile.idxC], BOMBE)){
            idxBombe[2] = ludophile.idxL+1;
            idxBombe[3] = ludophile.idxC;
        }

        //Si la bombe se trouve à droite du joueur
        if(equals(map[idxBombe[4]][idxBombe[5]], ludophile.personnage)){
            delay(500);
            map[idxBombe[4]][idxBombe[5]] = EXPLOSION;
            
            if(ludophile.nbBouclier>0){
                ludophile.nbBouclier--;
            } else {
                nbVie--;
            }
            
            afficherMap(map, ludophile);
            println();
            kaomijiOrateurln(GRAS + ROUGE + "Attention ! Tu as touché une bombe, fais super gaffe !" + RESET);
            delay(500);
            map[idxBombe[4]][idxBombe[5]] = ludophile.personnage;
            afficherMap(map, ludophile);
            println();
            idxBombe[4] = 0;
            idxBombe[5] = 0;
        }

        if(ludophile.idxC<length(map)-1 && equals(map[ludophile.idxL][ludophile.idxC+1], BOMBE)){
            idxBombe[4] = ludophile.idxL;
            idxBombe[5] = ludophile.idxC+1;
        }

        //Si la bombe se trouve à gauche du joueur
        if(equals(map[idxBombe[6]][idxBombe[7]], ludophile.personnage)){
            delay(500);
            map[idxBombe[6]][idxBombe[7]] = EXPLOSION;
            
            if(ludophile.nbBouclier>0){
                ludophile.nbBouclier--;
            } else {
                nbVie--;
            }
            
            afficherMap(map, ludophile);
            println();
            kaomijiOrateurln(GRAS + ROUGE + "Attention ! Tu as touché une bombe, fais super gaffe !" + RESET);
            delay(500);
            map[idxBombe[6]][idxBombe[7]] = ludophile.personnage;
            afficherMap(map, ludophile);
            println();
            idxBombe[6] = 0;
            idxBombe[7] = 0;
        }

        if(ludophile.idxC>0 && equals(map[ludophile.idxL][ludophile.idxC-1], BOMBE)){
            idxBombe[6] = ludophile.idxL;
            idxBombe[7] = ludophile.idxC-1;
        }
    }

    //Effet des Arbres
    void effetArbre(Joueur ludophile, String[][] map){
        double probabilite = random();

        //Si l'arbre se trouve au-dessus du joueur
        if(equals(map[idxArbre[0]][idxArbre[1]], ludophile.personnage) && probabilite>0.5){
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
            println();
            idxArbre[0] = 0;
            idxArbre[1] = 0;
        }

        if(ludophile.idxL>0 && equals(map[ludophile.idxL-1][ludophile.idxC], ARBRE)){
            idxArbre[0] = ludophile.idxL-1;
            idxArbre[1] = ludophile.idxC;
        }

        //Si l'arbre se trouve en dessous du joueur
        if(equals(map[idxArbre[2]][idxArbre[3]], ludophile.personnage) && probabilite>0.5){
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
            println();
            idxArbre[2] = 0;
            idxArbre[3] = 0;
        }

        
        if(ludophile.idxL<length(map)-1 && equals(map[ludophile.idxL+1][ludophile.idxC], ARBRE)){
            idxArbre[2] = ludophile.idxL+1;
            idxArbre[3] = ludophile.idxC;
        }

        //Si l'arbre se trouve à droite du joueur
        if(equals(map[idxArbre[4]][idxArbre[5]], ludophile.personnage) && probabilite>0.5){
            delay(500);
            map[idxArbre[4]][idxArbre[5]] = COCO;
            
            if(ludophile.nbBouclier>0){
                ludophile.nbBouclier--;
            } else {
                nbVie--;
            }
            
            afficherMap(map, ludophile);
            println();
            kaomijiOrateurln(GRAS + ROUGE + "Oups ! Une énorme noix de coco est tombée sur ta tête, vraiment pas de chance !" + RESET);
            delay(500);
            map[idxArbre[4]][idxArbre[5]] = ludophile.personnage;
            afficherMap(map, ludophile);
            println();
            idxArbre[4] = 0;
            idxArbre[5] = 0;
        }

        
        if(ludophile.idxC<length(map)-1 && equals(map[ludophile.idxL][ludophile.idxC+1], ARBRE)){
            idxArbre[4] = ludophile.idxL;
            idxArbre[5] = ludophile.idxC+1;
        }

        //Si l'arbre se trouve à gauche du joueur
        if(equals(map[idxArbre[6]][idxArbre[7]], ludophile.personnage) && probabilite>0.5){
            delay(500);
            map[idxArbre[6]][idxArbre[7]] = COCO;
            
            if(ludophile.nbBouclier>0){
                ludophile.nbBouclier--;
            } else {
                nbVie--;
            }
            
            afficherMap(map, ludophile);
            println();
            kaomijiOrateurln(GRAS + ROUGE + "Oups ! Une énorme noix de coco est tombée sur ta tête, vraiment pas de chance !" + RESET);
            delay(500);
            map[idxArbre[6]][idxArbre[7]] = ludophile.personnage;
            afficherMap(map, ludophile);
            println();
            idxArbre[6] = 0;
            idxArbre[7] = 0;
        }

        
        if(ludophile.idxC>0 && equals(map[ludophile.idxL][ludophile.idxC-1], ARBRE)){
            idxArbre[6] = ludophile.idxL;
            idxArbre[7] = ludophile.idxC-1;
        }
    }

    //Effet de la lave
    void effetLave(Joueur ludophile, String[][] map){
        //Si la lave se trouve au-dessus du joueur
        if(equals(map[idxLave[0]][idxLave[1]], ludophile.personnage)){
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
            println();
            idxLave[0] = 0;
            idxLave[1] = 0;
        }

        if(ludophile.idxL>0 && equals(map[ludophile.idxL-1][ludophile.idxC], LAVE)){
            idxLave[0] = ludophile.idxL-1;
            idxLave[1] = ludophile.idxC;
        }

        //Si la lave se trouve en dessous du joueur
        if(equals(map[idxLave[2]][idxLave[3]], ludophile.personnage)){
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
            println();
            idxLave[2] = 0;
            idxLave[3] = 0;
        }
        
        if(ludophile.idxL<length(map)-1 && equals(map[ludophile.idxL+1][ludophile.idxC], LAVE)){
            idxLave[2] = ludophile.idxL+1;
            idxLave[3] = ludophile.idxC;
        }

        //Si la lave se trouve à droite du joueur
        if(equals(map[idxLave[4]][idxLave[5]], ludophile.personnage)){
            delay(500);
            map[idxLave[4]][idxLave[5]] = BRULE;
            
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
            map[idxLave[4]][idxLave[5]] = ludophile.personnage;
            afficherMap(map, ludophile);
            println();
            idxLave[4] = 0;
            idxLave[5] = 0;
        }

        if(ludophile.idxC<length(map)-1 && equals(map[ludophile.idxL][ludophile.idxC+1], LAVE)){
            idxLave[4] = ludophile.idxL;
            idxLave[5] = ludophile.idxC+1;
        }

        //Si la lave se trouve à gauche du joueur
        if(equals(map[idxLave[6]][idxLave[7]], ludophile.personnage)){
            delay(500);
            map[idxLave[6]][idxLave[7]] = BRULE;
            
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
            map[idxLave[6]][idxLave[7]] = ludophile.personnage;
            afficherMap(map, ludophile);
            println();
            idxLave[6] = 0;
            idxLave[7] = 0;
        }

        
        if(ludophile.idxC>0 && equals(map[ludophile.idxL][ludophile.idxC-1], LAVE)){
            idxLave[6] = ludophile.idxL;
            idxLave[7] = ludophile.idxC-1;
        }
    }

    //Effet des cartes
    void effetCarte(Joueur ludophile, String[][] map){
        String decision;
        int chiffre = (int) (random()*10)+1;

        //Si la carte se trouve au-dessus du joueur
        if(equals(map[idxCarte[0]][idxCarte[1]], ludophile.personnage)){
            kaomijiOrateurln(BLEU + "Tu as tiré une carte événement ! Attention, c’est du 50/50, tu peux soit gagner un bonus, soit subir un malus, tout dépend de ta réponse !" + RESET);
            kaomijiOrateurln("Je pense à un chiffre entre 1 et 10, tu crois qu'il est pair ou impair ?");
            kaomijiOrateur("Choix [PAIR ; IMPAIR]: ");
            decision = readString();

            while(!equals(decision, "PAIR") && !equals(decision, "IMPAIR")){
                kaomijiOrateur(ROUGE + "Non, tu dois choisir entre [PAIR, IMPAIR] tout en majuscule : " + RESET);
                decision = readString();
            }

            kaomijiOrateurln("Le chiffre auquel je pensais est : " + chiffre);

            if((chiffre%2 == 0 && equals(decision, "PAIR")) || (chiffre%2 == 1 && equals(decision, "IMPAIR"))){
                kaomijiOrateurln(GRAS + VERT + "TU AS UN BONUS !" + RESET);
                bonus(ludophile, map);
            } else {
                kaomijiOrateurln(GRAS + ROUGE + "TU AS UN MALUS !" + RESET);
                malus(ludophile, map);
            }

            idxCarte[0] = 0;
            idxCarte[1] = 0;
        }

        if(ludophile.idxL>0 && equals(map[ludophile.idxL-1][ludophile.idxC], CARTE)){
            idxCarte[0] = ludophile.idxL-1;
            idxCarte[1] = ludophile.idxC;
        }

        //Si la carte se trouve en dessous du joueur
        if(equals(map[idxCarte[2]][idxCarte[3]], ludophile.personnage)){
            kaomijiOrateurln(BLEU + "Tu as tiré une carte événement ! Attention, c’est du 50/50, tu peux soit gagner un bonus, soit subir un malus, tout dépend de ta réponse !" + RESET);
            kaomijiOrateurln("Je pense à un chiffre entre 1 et 10, tu crois qu'il est pair ou impair ?");
            kaomijiOrateur("Choix [PAIR ; IMPAIR]: ");
            decision = readString();

            while(!equals(decision, "PAIR") && !equals(decision, "IMPAIR")){
                kaomijiOrateur(ROUGE + "Non, tu dois choisir entre [PAIR, IMPAIR] tout en majuscule : " + RESET);
                decision = readString();
            }

            kaomijiOrateurln("Le chiffre auquel je pensais est : " + chiffre);

            if((chiffre%2 == 0 && equals(decision, "PAIR")) || (chiffre%2 == 1 && equals(decision, "IMPAIR"))){
                kaomijiOrateurln(GRAS + VERT + "TU AS UN BONUS !" + RESET);
                bonus(ludophile, map);
            } else {
                kaomijiOrateurln(GRAS + ROUGE + "TU AS UN MALUS !" + RESET);
                malus(ludophile, map);
            }

            idxCarte[2] = 0;
            idxCarte[3] = 0;
        }

        
        if(ludophile.idxL<length(map)-1 && equals(map[ludophile.idxL+1][ludophile.idxC], CARTE)){
            idxCarte[2] = ludophile.idxL+1;
            idxCarte[3] = ludophile.idxC;
        }

        //Si la carte se trouve à droite du joueur
        if(equals(map[idxCarte[4]][idxCarte[5]], ludophile.personnage)){
            kaomijiOrateurln(BLEU + "Tu as tiré une carte événement ! Attention, c’est du 50/50, tu peux soit gagner un bonus, soit subir un malus, tout dépend de ta réponse !" + RESET);
            kaomijiOrateurln("Je pense à un chiffre entre 1 et 10, tu crois qu'il est pair ou impair ?");
            kaomijiOrateur("Choix [PAIR ; IMPAIR]: ");
            decision = readString();

            while(!equals(decision, "PAIR") && !equals(decision, "IMPAIR")){
                kaomijiOrateur(ROUGE + "Non, tu dois choisir entre [PAIR, IMPAIR] tout en majuscule : " + RESET);
                decision = readString();
            }

            kaomijiOrateurln("Le chiffre auquel je pensais est : " + chiffre);

            if((chiffre%2 == 0 && equals(decision, "PAIR")) || (chiffre%2 == 1 && equals(decision, "IMPAIR"))){
                kaomijiOrateurln(GRAS + VERT + "TU AS UN BONUS !" + RESET);
                bonus(ludophile, map);
            } else {
                kaomijiOrateurln(GRAS + ROUGE + "TU AS UN MALUS !" + RESET);
                malus(ludophile, map);
            }

            idxCarte[4] = 0;
            idxCarte[5] = 0;
        }
        
        if(ludophile.idxC<length(map)-1 && equals(map[ludophile.idxL][ludophile.idxC+1], CARTE)){
            idxCarte[4] = ludophile.idxL;
            idxCarte[5] = ludophile.idxC+1;
        }

        //Si la carte se trouve à gauche du joueur
        if(equals(map[idxCarte[6]][idxCarte[7]], ludophile.personnage)){
            kaomijiOrateurln(BLEU + "Tu as tiré une carte événement ! Attention, c’est du 50/50, tu peux soit gagner un bonus, soit subir un malus, tout dépend de ta réponse !" + RESET);
            kaomijiOrateurln("Je pense à un chiffre entre 1 et 10, tu crois qu'il est pair ou impair ?");
            kaomijiOrateur("Choix [PAIR ; IMPAIR]: ");
            decision = readString();

            while(!equals(decision, "PAIR") && !equals(decision, "IMPAIR")){
                kaomijiOrateur(ROUGE + "Non, tu dois choisir entre [PAIR, IMPAIR] tout en majuscule : " + RESET);
                decision = readString();
            }

            kaomijiOrateurln("Le chiffre auquel je pensais est : " + chiffre);

            if((chiffre%2 == 0 && equals(decision, "PAIR")) || (chiffre%2 == 1 && equals(decision, "IMPAIR"))){
                kaomijiOrateurln(GRAS + VERT + "TU AS UN BONUS !" + RESET);
                bonus(ludophile, map);
            } else {
                kaomijiOrateurln(GRAS + ROUGE + "TU AS UN MALUS !" + RESET);
                malus(ludophile, map);
            }

            idxCarte[6] = 0;
            idxCarte[7] = 0;
        }

        
        if(ludophile.idxC>0 && equals(map[ludophile.idxL][ludophile.idxC-1], CARTE)){
            idxCarte[6] = ludophile.idxL;
            idxCarte[7] = ludophile.idxC-1;
        }
    }



    /* ================================================================================== */
    /* Tout ce qui concerne les bonus, les malus et ce que dit Kaomiji en fonction des PV */
    /* ================================================================================== */

    //Les bonus
    void bonus(Joueur ludophile, String[][] map){
        String saisie;
        int choix;

        kaomijiOrateurln("Youpi ! Tu as gagné un bonus ! 🎉");
        kaomijiOrateurln("Choisis ce que tu veux parmi les bonus suivants ! (Tant que tu n'as pas atteint leur max.) : ");
        println(espacement(maitreKaomiji(nbVie) + " - ") + "0: Rien.");
        println(espacement(maitreKaomiji(nbVie) + " - ") + "1: Récuperez des PV (max. 10): ❤️");
        println(espacement(maitreKaomiji(nbVie) + " - ") + "2: Obtenir un bouclier (max. 5): 🛡️");
        println(espacement(maitreKaomiji(nbVie) + " - ") + "3: Obtenir une immunité contre des malus (max. 1): 💊");
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
                println();
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
                println();
            } else {
                kaomijiOrateurln("Ton nombre de bouclier est déjà au max... On recommence !");
                bonus(ludophile, map);
            }
        } else if(choix == 3){
            kaomijiOrateurln("Activation de l'immunité...");
            map[ludophile.idxL][ludophile.idxC] = "💊";
            ludophile.immunite = true;
            afficherMap(map, ludophile);
            delay(1000);
            map[ludophile.idxL][ludophile.idxC] = ludophile.personnage;
            afficherMap(map, ludophile);
            println();
        }
    }

    //Les malus
    void malus(Joueur ludophile, String[][] map){
        Objectif but = newObjectif();

        kaomijiOrateurln("Désolé, mais tu as un malus !");
        kaomijiOrateurln("Le plus rigolo, c’est que c’est moi qui décide !");
        kaomijiOrateurln(ROUGE + "QUE VAIS-JE FAIRE?!" + RESET);
        
        int random = (int) (random()*3)+1;
        delay(1000);

        if(ludophile.immunite == true){
            kaomijiOrateurln(VERT + "Mais malheureusement... Tu as une immunité contre les malus... Quelle tristesse !" + RESET);
            kaomijiOrateurln(JAUNE + "Désactivation de l'immunité..." + RESET);
            ludophile.immunite = false;
            afficherMap(map, ludophile);
        } else {
            if(random == 1){
                kaomijiOrateurln("Un joli petit météore va te faire plaisir !");
                map[ludophile.idxL][ludophile.idxC] = "☄️";
                afficherMap(map, ludophile);
                delay(1000);
                map[ludophile.idxL][ludophile.idxC] = "💥";
                nbVie = nbVie - 2;
                afficherMap(map, ludophile);
                delay(1000);
                map[ludophile.idxL][ludophile.idxC] = ludophile.personnage;
                afficherMap(map, ludophile);
                kaomijiOrateurln(JAUNE + "Attention, tu as perdu 2 ❤️" + RESET);
                println();
            } else if(random == 2) {
                kaomijiOrateurln("Et si on déplaçait le drapeau ? :)");
                kaomijiOrateurln("Pourquoi je demande ton avis... ?");

                map[but.idxObjectifLigne][but.idxObjectifColonne] = CHEMIN;

                but.idxObjectifLigne = (int)(random()*length(map, 1)-1)+1;
                but.idxObjectifColonne = (int)(random()*length(map, 2)-1)+1;

                map[but.idxObjectifLigne][but.idxObjectifColonne] = but.DRAPEAU;  

                afficherMap(map, ludophile);
                kaomijiOrateurln("TADAAAA, bonne chance !");   
                println();       
            } else if(random == 3){
                kaomijiOrateurln("Connais-tu le spinjitsu ?");
                kaomijiOrateurln(ROUGE + "ATTENTION... TORNADE !" + RESET);
                map[ludophile.idxL][ludophile.idxC] = "🌪️";
                afficherMap(map, ludophile);

                for(int cpt = 0; cpt<2; cpt++){
                    int direction = (int) (random()*5)+1;
                    for(int clc = 0; clc<direction; clc++){
                        if(direction<2){
                            avancerNord(ludophile, map);
                        } else if(direction == 3){
                            avancerOuest(ludophile, map);
                        } else if(direction>3){
                            avancerSud(ludophile, map);
                            avancerEst(ludophile, map);
                        }
                    }
                }

                map[ludophile.idxL][ludophile.idxC] = ludophile.personnage;
                afficherMap(map, ludophile);
                kaomijiOrateurln(JAUNE + "Tu dois avoir des vertiges, et tu peux pas bouger pendant 3 secondes !" + RESET);
                delay(3000);
                println();
            }
        }
    }



    /* ===================================================== */
    /* Tout ce qui concerne begin() lors du lancement du jeu */
    /* ===================================================== */
    void begin(Joueur ludophile, Objectif but){
        creationPersonnage(ludophile);
        tutoriel(ludophile, but);
        reglement(ludophile);
    }

    //Introduction et création du personnage lors du démarrage du jeu
    void creationPersonnage(Joueur ludophile){
        String choix;
        kaomijiOrateurln("Bienvenue dans VivreOuSurvivre ! Dans ce jeu, tu vas apprendre les bases des algorithmes en t'amusant.");
        kaomijiOrateur("Je me présente, je suis le maître du jeu : Kaomiji, ton super compagnon ! Et toi, qui es-tu ? ");
        ludophile.nom = readString();

        if(equals(ludophile.nom, "")){
            ludophile.nom = "Nameless";
        }

        delay(1000);
        kaomijiOrateurln(ludophile.nom + "? Super ton nom ! Avant de commencer à t'apprendre les bases des algorithmes, il faut d'abord créer ton personnage.");
        genreJoueur(ludophile);
        delay(1000);
        personnageJoueur(ludophile);
        delay(1000);
        recaputilatif(ludophile);
        delay(1000);
        println();
    }

    //Tutoriel Global
    void tutoriel(Joueur ludophile, Objectif but){
        String[][] map = new String[5][5];

        kaomijiOrateur("Souhaitez-vous passer un tutoriel? [Oui (O); Non (N)] : ");
        String choix = readString();

        if(equals(choix, "") || equals(choix, "O") || equals(choix, "Oui")){
            println();
            delay(1000);
            kaomijiOrateurln("Ce que tu dois savoir ET retenir, " + BLEU + "c'est que les ordinateurs font exactement TOUT ce qu'on leur dit" + RESET + ", sans poser de questions.");
            kaomijiOrateurln("Heureusement, les langages de programmation ont des règles de sécurité qui évitent de faire des bêtises et de casser ton ordinateur.\n");
            kaomijiOrateurln(BLEU + "Pour ce tutoriel, je te conseille d'utiliser le pavé numérique pour entrer les valeurs des déplacements qui lui correspondent." + RESET);
            kaomijiOrateurln("PS: Tu ne gagnes pas de points de réussite. ;^;");
            kaomijiOrateur("Appuie sur (ENTER) pour commencer le tutoriel !");
            choix = readString();
            println();
            delay(1000);
            avancerTutoriel(ludophile, but, map);
            println();
            delay(1000);
            droiteTutoriel(ludophile, but, map);
            println();
            delay(1000);
            gaucheTutoriel(ludophile, but, map);
            println();
            delay(1000);
            basTutoriel(ludophile, but, map);
            println();
            delay(1000);
            kaomijiOrateurln("Avant d'aller plus loin, sache qu'il y a deux types de boucles :\n");
            println(espacement(maitreKaomiji(nbVie) + " - ") + BLEU + "La boucle \"pour\" : C'est comme quand tu fais une tâche plusieurs fois.\n" + RESET +
                             espacement(maitreKaomiji(nbVie) + " - ") + "Par exemple, \"Fais ceci 5 fois\". Tu répètes une action un nombre précis de fois.\n");
            delay(1000);
            println(espacement(maitreKaomiji(nbVie) + " - ") + BLEU + "La boucle \"tant que\" : C'est quand tu fais quelque chose encore et encore, tant qu'une condition est vraie.\n" + RESET +
                             espacement(maitreKaomiji(nbVie) + " - ") + "Par exemple, \"Continue de sauter tant que tu n'as pas touché le sol\". Tu répètes jusqu'à ce que ça change.\n");
            delay(1000);
            kaomijiOrateur("Si tu es prêt à commencer avec les boucles à compteur, aka la boucle \"pour\", appuie sur la touche (ENTER) de ton clavier ! ");
            choix = readString();
            println();
            delay(1000);
            boucleCompteurTutoriel(ludophile, but, map);
            println();
            delay(1000);
            boucleWhileTutoriel(ludophile, but, map);
            println();
            delay(1000);
        }
        println();
    }

    //Règlement
    void reglement(Joueur ludophile){
        String confirmateur;
        kaomijiOrateurln(GRAS + "Avant de commencer à jouer, je veux vous rappeler les règles.\n" + RESET);
        delay(1000);
        println(espacement(maitreKaomiji(nbVie)) + " • Drapeau : Le joueur doit atteindre le drapeau \"🚩 \" en utilisant un algorithme, en s'aidant d'un ensemble d'outils mis à sa disposition.");
        delay(1000);
        println(espacement(maitreKaomiji(nbVie)) + " • Début de la carte : Si le drapeau n’est pas atteint à la fin de l'algorithme, le joueur devra recommencer depuis le début de la carte.");
        delay(1000);
        println(espacement(maitreKaomiji(nbVie)) + " • Obstacles : Les bombes explosées ou autres éléments de la carte ne réapparaîtront pas une fois rencontrés.");
        delay(1000);
        println(espacement(maitreKaomiji(nbVie)) + " • Point de Vie (❤️ ) : Le joueur commence avec un total de 10 ❤️ .");
        delay(1000);
        println(espacement(maitreKaomiji(nbVie)) + " • Déplacement : Le joueur peut se déplacer librement sur les chemins \"⬛ \" comme bon lui semble.");
        delay(1000);
        println(espacement(maitreKaomiji(nbVie)) + " • Bombe : Si le joueur touche une bombe \"💣 \", il perd 1 ❤️ .");
        delay(1000);
        println(espacement(maitreKaomiji(nbVie)) + " • Lave : Si le joueur touche la lave \"🌋 \", il perd 5 de ses ❤️ .");
        delay(1000);
        println(espacement(maitreKaomiji(nbVie)) + " • Arbre : Si le joueur essaie de traverser un arbre \"🌴 \", il a 50 % de chances de prendre des dégâts.");
        delay(1000);
        println(espacement(maitreKaomiji(nbVie)) + " • Montagne : Le joueur ne peut pas traverser la montagne \"🗻 \".");
        delay(1000);
        println(espacement(maitreKaomiji(nbVie)) + " • Cartes événements : Des cartes événements \"🎴 \" peuvent donner des bonus ou des malus au joueur.");
        delay(1000);
        println(espacement(maitreKaomiji(nbVie)) + " • Bouclier : Le bouclier \"🛡️ \" protège le joueur contre tous les dégâts, agissant comme un deuxième PV (max. 5).");
        delay(1000);
        println(espacement(maitreKaomiji(nbVie)) + " • Immunité : L'immunité contre les malus n'est pas stackable.");
        delay(1000);
        println(espacement(maitreKaomiji(nbVie)) + " • Pertes de ❤️  : Lorsque le joueur perd des ❤️ , Kaomiji s’énerve. Si les ❤️  du joueur atteignent 0, soit \"💔 \", Kaomiji abattra le joueur.\n");
        delay(1000);
        kaomijiOrateur(GRAS + "Quand tu seras prêt et que tu auras bien lu les règles, appuie sur [ENTER] pour commencer à jouer." + RESET);
        confirmateur = readString();
        delay(1000);
    }





    /* ======================================================== */
    /* Tout ce qui concerne tutoriel() lors du lancement du jeu */
    /* ======================================================== */

    //Tutoriel pour avancer
    void avancerTutoriel(Joueur ludophile, Objectif but, String[][] map){
        remplissageMap(map);

        but.idxObjectifLigne = length(map,1)/2;
        but.idxObjectifColonne = length(map,2)/2;

        ludophile.idxL = length(map,1)-1;
        ludophile.idxC = length(map,2)/2;

        map[but.idxObjectifLigne][but.idxObjectifColonne] = but.DRAPEAU;
        map[ludophile.idxL][ludophile.idxC] = ludophile.personnage;

        kaomijiOrateurln("On va commencer doucement. Avance jusqu'à atteindre le drapeau rouge !");
        kaomijiOrateurln("Pour t'entraîner, appuie sur la touche (8) du clavier pour avancer !");
        afficherMap(map, ludophile);
        println();

        while(objectifAtteint(ludophile, map, but)){
            print("(8):⬆️ \nChoix : ");
            String saisie = readString();
            int choix = verificationString(saisie);
            if(choix == 8){
                avancerNord(ludophile, map);
            }else{
                kaomijiOrateurln(JAUNE + "Tu ne t'es pas déplacé. Assure-toi d'appuyer sur le bon bouton pour te déplacer !" + RESET);
            }
        }

        kaomijiOrateurln(VERT + "Félicitations ! Maintenant, on passe aux déplacements vers la droite." + RESET);
    }

    //Tutoriel pour déplacement droite
    void droiteTutoriel(Joueur ludophile, Objectif but, String[][] map){
        remplissageMap(map);

        but.idxObjectifLigne = 0;
        but.idxObjectifColonne = length(map,2)-1;

        ludophile.idxL = length(map,1)-1;
        ludophile.idxC = length(map,2)/2;

        map[but.idxObjectifLigne][but.idxObjectifColonne] = but.DRAPEAU;
        map[ludophile.idxL][ludophile.idxC] = ludophile.personnage;

        kaomijiOrateurln("Déplace-toi jusqu'à ce que tu atteignes le drapeau rouge !");
        kaomijiOrateurln("Pour t'entraîner, appuie sur la touche (8) et (6) du clavier pour te déplacer!");
        afficherMap(map, ludophile);
        println();

        while(objectifAtteint(ludophile, map, but)){
            print("(8):⬆️   ; (6):➡️\nChoix : ");
            String saisie = readString();
            int choix = verificationString(saisie);
            if(choix == 8){
                avancerNord(ludophile, map);
            }else if(choix == 6){
                avancerEst(ludophile, map);
            }else{
                kaomijiOrateurln(JAUNE + "Tu ne t'es pas déplacé. Assure-toi d'appuyer sur le bon bouton pour te déplacer !" + RESET);
            }
        }

        kaomijiOrateurln(VERT + "Félicitations ! Maintenant, on passe aux déplacements vers la gauche." + RESET);
    }

    //Tutoriel déplacement vers la gauche
    void gaucheTutoriel(Joueur ludophile, Objectif but, String[][] map){
        remplissageMap(map);

        but.idxObjectifLigne = 0;
        but.idxObjectifColonne = 0;

        ludophile.idxL = length(map,1)-1;
        ludophile.idxC = length(map,2)/2;

        map[but.idxObjectifLigne][but.idxObjectifColonne] = but.DRAPEAU;
        map[ludophile.idxL][ludophile.idxC] = ludophile.personnage;

        kaomijiOrateurln("Déplace-toi jusqu'à ce que tu atteignes le drapeau rouge !");
        kaomijiOrateurln("Pour t'entraîner, appuie sur la touche (8), (6) et (4) du clavier pour te déplacer!");
        afficherMap(map, ludophile);
        println();

        while(objectifAtteint(ludophile, map, but)){
            print("(8):⬆️   ; (6):➡️   ; (4):⬅️\nChoix : ");
            String saisie = readString();
            int choix = verificationString(saisie);
            if(choix == 8){
                avancerNord(ludophile, map);
            }else if(choix == 6){
                avancerEst(ludophile, map);
            }else if(choix == 4){
                avancerOuest(ludophile, map);
            }else{
                kaomijiOrateurln(JAUNE + "Tu ne t'es pas déplacé. Assure-toi d'appuyer sur le bon bouton pour te déplacer !" + RESET);
            }
        }

        kaomijiOrateurln(VERT + "Félicitations ! Maintenant, on passe aux déplacements vers le bas." + RESET);
    }

    //Tutoriel déplacement vers le bas
    void basTutoriel(Joueur ludophile, Objectif but, String[][] map){
        remplissageMap(map);

        but.idxObjectifLigne = length(map,1)-1;
        but.idxObjectifColonne = length(map,2)-1;

        ludophile.idxL = 0;
        ludophile.idxC = 0;

        map[but.idxObjectifLigne][but.idxObjectifColonne] = but.DRAPEAU;
        map[ludophile.idxL][ludophile.idxC] = ludophile.personnage;

        kaomijiOrateurln("Déplace-toi jusqu'à ce que tu atteignes le drapeau rouge !");
        kaomijiOrateurln("Pour t'entraîner, appuie sur la touche (8), (6), (4) et (2) du clavier pour te déplacer!");
        afficherMap(map, ludophile);
        println();

        while(objectifAtteint(ludophile, map, but)){
            print("(8):⬆️   ; (6):➡️   ; (4):⬅️   ; (2):⬇️\nChoix : ");
            String saisie = readString();
            int choix = verificationString(saisie);
            if(choix == 8){
                avancerNord(ludophile, map);
            }else if(choix == 6){
                avancerEst(ludophile, map);
            }else if(choix == 4){
                avancerOuest(ludophile, map);
            }else if(choix == 2){
                avancerSud(ludophile, map);
            }else{
                kaomijiOrateurln(JAUNE + "Tu ne t'es pas déplacé. Assure-toi d'appuyer sur le bon bouton pour te déplacer !" + RESET);
            }
        }

        kaomijiOrateurln(VERT + "Félicitations ! Maintenant, on passe aux boucles." + RESET);
    }

    //Tutoriel déplacement en boucle à compteur
    void boucleCompteurTutoriel(Joueur ludophile, Objectif but, String[][] map){
        remplissageMap(map);

        but.idxObjectifLigne = length(map,1)-1;
        but.idxObjectifColonne = length(map,2)-1;

        ludophile.idxL = 0;
        ludophile.idxC = 0;

        map[but.idxObjectifLigne][but.idxObjectifColonne] = but.DRAPEAU;
        map[ludophile.idxL][ludophile.idxC] = ludophile.personnage;

        kaomijiOrateurln("Déplace-toi jusqu'à ce que tu atteignes le drapeau rouge !");
        kaomijiOrateurln("Pour t'entraîner, appuie sur la touche (1) du clavier pour te déplacer!");
        afficherMap(map, ludophile);
        println();

        while(objectifAtteint(ludophile, map, but)){
            print("(1):🔁\nChoix : ");
            String saisie = readString();
            int choix = verificationString(saisie);
            if(choix == 1){
                boucleCompteur(ludophile, map);
            }else{
                kaomijiOrateurln(JAUNE + "Tu ne t'es pas déplacé. Assure-toi d'appuyer sur le bon bouton pour te déplacer !" + RESET);
            }
        }

        kaomijiOrateurln(VERT + "Félicitations ! Est-ce que ce n'est pas plus facile d'utiliser des boucles pour se déplacer ?" + RESET);
        kaomijiOrateurln("Maintenant, on passe aux boucles événementielles, aka la boucle \"tant que\" !");
    }

    void boucleWhileTutoriel(Joueur ludophile, Objectif but, String[][] map){
        remplissageMap(map);

        but.idxObjectifLigne = length(map,1)-1;
        but.idxObjectifColonne = length(map,2)-1;

        ludophile.idxL = 0;
        ludophile.idxC = 0;

        map[but.idxObjectifLigne][but.idxObjectifColonne] = but.DRAPEAU;
        map[ludophile.idxL][ludophile.idxC] = ludophile.personnage;
        map[length(map,1)-1][0] = MONTAGNE;
        map[length(map,1)-2][length(map,2)-1] = MONTAGNE;

        kaomijiOrateurln("Déplace-toi jusqu'à ce que tu atteignes le drapeau rouge !");
        kaomijiOrateurln("Pour t'entraîner, appuie sur la touche (8), (6), (4), (2) et (3) du clavier pour te déplacer!");
        afficherMap(map, ludophile);
        println();

        while(objectifAtteint(ludophile, map, but)){
            print("(8):⬆️   ; (6):➡️   ; (4):⬅️   ; (2):⬇️   ; (3):🔄️\nChoix : ");

            String saisie = readString();
            int choix = verificationString(saisie);

            if(choix == 8){
                avancerNord(ludophile, map);
            } else if(choix == 6){
                avancerEst(ludophile, map);
            } else if(choix == 4){
                avancerOuest(ludophile, map);
            } else if(choix == 2){
                avancerSud(ludophile, map);
            } else if(choix == 3){
                boucleWhile(ludophile, map);
            } else {
                kaomijiOrateurln(JAUNE + "Tu ne t'es pas déplacé. Assure-toi d'appuyer sur le bon bouton pour te déplacer !" + RESET);
            }
        }

        kaomijiOrateurln(VERT + "Félicitations ! Encore un petit effort, tu es presque à la fin du tutoriel !" + RESET);
        kaomijiOrateurln("Maintenant, on passe aux alternatives ^_^");
    }



    /* ======================================================================= */
    /* Tout ce qui conditon booléenne pour les boucles while() et alternatives */
    /* ======================================================================= */
    
    //Condition : Vers le nord, c'est un CHEMIN?
    boolean estCheminNord(Joueur ludophile, String[][] map){
        if(ludophile.idxL == 0){
            return false;
        }

        if(equals(map[ludophile.idxL-1][ludophile.idxC], CHEMIN)){
            return true;
        }

        return false;
    }

    void testEstCheminNord(){
        Joueur ludophile = newJoueur();
        String[][] map;

        ludophile.idxL = 1;
        ludophile.idxC = 1;

        map = new String[][]{{CHEMIN, CHEMIN, CHEMIN},
                             {CHEMIN, ludophile.personnage, CHEMIN},
                             {CHEMIN, CHEMIN, CHEMIN}};
        assertTrue(estCheminNord(ludophile, map));

        map = new String[][]{{CHEMIN, BOMBE, CHEMIN},
                             {CHEMIN, ludophile.personnage, CHEMIN},
                             {CHEMIN, CHEMIN, CHEMIN}};
        assertFalse(estCheminNord(ludophile, map));
    }

    //Condition : Vers le sud, c'est un CHEMIN?
    boolean estCheminSud(Joueur ludophile, String[][] map){
        if(ludophile.idxL == length(map, 1)-1){
            return false;
        }

        if(equals(map[ludophile.idxL+1][ludophile.idxC], CHEMIN)){
            return true;
        }

        return false;
    }

    void testEstCheminSud(){
        Joueur ludophile = newJoueur();
        String[][] map;

        ludophile.idxL = 1;
        ludophile.idxC = 1;

        map = new String[][]{{CHEMIN, CHEMIN, CHEMIN},
                             {CHEMIN, ludophile.personnage, CHEMIN},
                             {CHEMIN, CHEMIN, CHEMIN}};
        assertTrue(estCheminSud(ludophile, map));

        map = new String[][]{{CHEMIN, CHEMIN, CHEMIN},
                             {CHEMIN, ludophile.personnage, CHEMIN},
                             {CHEMIN, ARBRE, CHEMIN}};
        assertFalse(estCheminSud(ludophile, map));
    }

    //Condition : Vers l'est, c'est un CHEMIN?
    boolean estCheminEst(Joueur ludophile, String[][] map){
        if(ludophile.idxC == length(map, 2)-1){
            return false;
        }

        if(equals(map[ludophile.idxL][ludophile.idxC+1], CHEMIN)){
            return true;
        }

        return false;
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
    }

    //Condition : Vers l'ouest, c'est un CHEMIN?
    boolean estCheminOuest(Joueur ludophile, String[][] map){
        if(ludophile.idxC == 0){
            return false;
        }

        if(equals(map[ludophile.idxL][ludophile.idxC-1], CHEMIN)){
            return true;
        }

        return false;
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
    }



    /* ============================================================= */
    /* Choix de déplacement globales, boucle for() et boucle while() */
    /* ============================================================= */

    //Choix de déplacement global
    void deplacement(Joueur ludophile, String[][] map){
        String saisie;
        int choix;

        kaomijiOrateur("Choisis un chiffre parmi ceux proposés, qui correspond au déplacement que tu veux faire !\n" + 
                        espacement(maitreKaomiji(nbVie) + " - ") + "(8):⬆️   ; (6):➡️   ; (4):⬅️   ; (2):⬇️   ; (1):🔁   ; (3):🔄️\nChoix : ");
        saisie = readString();
        choix = verificationString(saisie);
        
        if(choix == 8){
            avancerNord(ludophile, map);
        } else if(choix == 6){
            avancerEst(ludophile, map);
        } else if(choix == 4){
            avancerOuest(ludophile, map);
        } else if(choix == 2){
            avancerSud(ludophile, map);
        } else if(choix == 1){
            boucleCompteur(ludophile, map);
        } else if(choix == 3) {
            boucleWhile(ludophile, map);
        } else {
            kaomijiOrateurln(JAUNE + "Tu ne t'es pas déplacé. Assure-toi d'appuyer sur le bon bouton pour te déplacer !" + RESET);
        }
    }

    //Choix de déplacement pour les boucles for()
    void choixDeplacementBoucle(int nbChoix, int nbCases, Joueur ludophile, String[][] map){
        if(nbChoix == 8){
            for(int cpt=0; cpt<nbCases; cpt++){
                if(!deplacementPossibleNord(ludophile, map)){
                    cpt = nbCases;
                    kaomijiOrateurln(JAUNE + "Ce déplacement n'est pas possible. N'oublie pas, l'ordinateur fait juste ce que tu lui dis de faire, même si ça n'a pas l'air correcte !" + RESET);
                } else {
                    avancerNord(ludophile, map);
                    delay(500);
                }
            }
        } else if(nbChoix == 6){
            for(int cpt=0; cpt<nbCases; cpt++){
                if(!deplacementPossibleEst(ludophile, map)){
                    cpt = nbCases;
                    kaomijiOrateurln(JAUNE + "Ce déplacement n'est pas possible. N'oublie pas, l'ordinateur fait juste ce que tu lui dis de faire, même si ça n'a pas l'air correcte !" + RESET);
                } else {
                    avancerEst(ludophile, map);
                    delay(500);
                }
            }
        } else if(nbChoix == 4){
            for(int cpt=0; cpt<nbCases; cpt++){
                if(!deplacementPossibleOuest(ludophile, map)){
                    cpt = nbCases;
                    kaomijiOrateurln(JAUNE + "Ce déplacement n'est pas possible. N'oublie pas, l'ordinateur fait juste ce que tu lui dis de faire, même si ça n'a pas l'air correcte !" + RESET);
                } else {
                    avancerOuest(ludophile, map);
                    delay(500);
                }
            }
        } else if(nbChoix == 2){
            for(int cpt=0; cpt<nbCases; cpt++){
                if(!deplacementPossibleSud(ludophile, map)){
                    cpt = nbCases;
                    kaomijiOrateurln(JAUNE + "Ce déplacement n'est pas possible. N'oublie pas, l'ordinateur fait juste ce que tu lui dis de faire, même si ça n'a pas l'air correcte !" + RESET);
                } else {
                    avancerSud(ludophile, map);
                    delay(500);
                }
            }
        } else {
            kaomijiOrateurln(JAUNE + "Tu ne t'es pas déplacé. Assure-toi d'appuyer sur le bon bouton pour te déplacer !" + RESET);
        }
    }

    //Choix de condition et déplacement pour les boucles while()
    void choixDeplacementWhile(int nbChoix, Joueur ludophile, String[][] map){
        String choix = "";

        if(nbChoix == 8){
            kaomijiOrateurln("Tu veux aller vers ⬆️   Tant Que :");
            println(espacement(maitreKaomiji(nbVie) + " - ") + "(a) : La case devant moi est un " + CHEMIN);
            println(espacement(maitreKaomiji(nbVie) + " - ") + "(*) : D'autres conditions seront ajoutées bientôt !");
            print(espacement(maitreKaomiji(nbVie) + " - ") + "Choix : ");
            choix = readString();

            while(!equals(choix, "a")){
                kaomijiOrateur("Tu n'as pas choisi une lettre qui correspond à l'une des conditions ci-dessus, essaie encore : ");
                choix = readString();
            }

            if(equals(choix, "a")){
                if(!estCheminNord(ludophile, map)){
                    kaomijiOrateurln(JAUNE + "Ce déplacement n'est pas possible. N'oublie pas, l'ordinateur fait juste ce que tu lui dis de faire, même si ça n'a pas l'air correcte !" + RESET);
                }
                while(estCheminNord(ludophile, map)){
                    avancerNord(ludophile, map);
                    delay(500);
                }
            }
            
        } else if(nbChoix == 6){
            kaomijiOrateurln("Tu veux aller vers ➡️   Tant Que :");
            println(espacement(maitreKaomiji(nbVie) + " - ") + "(a) : La case devant moi est un " + CHEMIN);
            println(espacement(maitreKaomiji(nbVie) + " - ") + "(*) : D'autres conditions seront ajoutées bientôt !");
            print(espacement(maitreKaomiji(nbVie) + " - ") + "Choix : ");
            choix = readString();

            while(!equals(choix, "a")){
                kaomijiOrateur("Tu n'as pas choisi une lettre qui correspond à l'une des conditions ci-dessus, essaie encore : ");
                choix = readString();
            }

            if(equals(choix, "a")){
                if(!estCheminEst(ludophile, map)){
                    kaomijiOrateurln(JAUNE + "Ce déplacement n'est pas possible. N'oublie pas, l'ordinateur fait juste ce que tu lui dis de faire, même si ça n'a pas l'air correcte !" + RESET);
                }
                while(estCheminEst(ludophile, map)){
                    avancerEst(ludophile, map);
                    delay(500);
                }
            }
            
        } else if(nbChoix == 4){
            kaomijiOrateurln("Tu veux aller vers ⬅️   Tant Que :");
            println(espacement(maitreKaomiji(nbVie) + " - ") + "(a) : La case devant moi est un " + CHEMIN);
            println(espacement(maitreKaomiji(nbVie) + " - ") + "(*) : D'autres conditions seront ajoutées bientôt !");
            print(espacement(maitreKaomiji(nbVie) + " - ") + "Choix : ");
            choix = readString();

            while(!equals(choix, "a")){
                kaomijiOrateur("Tu n'as pas choisi une lettre qui correspond à l'une des conditions ci-dessus, essaie encore : ");
                choix = readString();
            }

            if(equals(choix, "a")){
                if(!estCheminEst(ludophile, map)){
                    kaomijiOrateurln(JAUNE + "Ce déplacement n'est pas possible. N'oublie pas, l'ordinateur fait juste ce que tu lui dis de faire, même si ça n'a pas l'air correcte !" + RESET);
                }
                while(estCheminOuest(ludophile, map)){
                    avancerOuest(ludophile, map);
                    delay(500);
                }
            }

        } else if(nbChoix == 2){
            kaomijiOrateurln("Tu veux aller vers ⬇️   Tant Que :");
            println(espacement(maitreKaomiji(nbVie) + " - ") + "(a) : La case devant moi est un " + CHEMIN);
            println(espacement(maitreKaomiji(nbVie) + " - ") + "(*) : D'autres conditions seront ajoutées bientôt !");
            print(espacement(maitreKaomiji(nbVie) + " - ") + "Choix : ");
            choix = readString();

            while(!equals(choix, "a")){
                kaomijiOrateur("Tu n'as pas choisi une lettre qui correspond à l'une des conditions ci-dessus, essaie encore : ");
                choix = readString();
            }

            if(equals(choix, "a")){
                if(!estCheminSud(ludophile, map)){
                    kaomijiOrateurln(JAUNE + "Ce déplacement n'est pas possible. N'oublie pas, l'ordinateur fait juste ce que tu lui dis de faire, même si ça n'a pas l'air correcte !" + RESET);
                }
                while(estCheminSud(ludophile, map)){
                    avancerSud(ludophile, map);
                    delay(500);
                }
            }
        } else {
            kaomijiOrateurln(JAUNE + "Tu ne t'es pas déplacé. Assure-toi d'appuyer sur le bon bouton pour te déplacer !" + RESET);
        }
    }
}