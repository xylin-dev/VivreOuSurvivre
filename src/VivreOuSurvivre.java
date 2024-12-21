class VivreOuSurvivre extends Program{
    
    /* ================= */
    /* VARIABLE GLOBALE  */
    /* ================= */

    //Elément de la map
    final String CHEMIN = "⬛";
    final String ARBRE = "🌳";
    final String MONTAGNE = "🗻";
    final String BOMBE = "💣";
    final String EXPLOSION = "💥";
    final String LAVE = "🔥";
    final String CARTE = "🎴";

    //Couleurs du texte selon de leurs fonctions
    final String VERT = "\u001B[32m"; //Réussite, objectif atteint.
    final String ROUGE = "\u001B[31m"; //Échec, erreur.
    final String BLEU = "\u001B[34m"; //Important, à ne pas négliger.
    final String JAUNE = "\u001B[33m"; //Alerte, attention nécessaire.
    String RESET = "\u001B[0m"; //Couleurs et Styles par défauts

    //Style du texte selon leurs fonctions
    final String GRAS = "\033[1m"; 
    final String SOULIGNE = "\033[4m";

    //Nb de Vie du Joueur ainsi que son Nb de Vie Precedent pour Kaomiji
    int nbVie = 10;
    int nbViePrecedent = 10;

    //Nb de Reussite (>5 = Apparition du Troll)
    int nbReussite = 0;

    void algorithm(){
        Joueur ludophile = newJoueur();
        Objectif but = newObjectif();

        String[][] map = new String[20][20];

        begin(ludophile, but);
        
        initialisationMap(map, ludophile, but);
        afficherMap(map, ludophile);
        println();

        while(!objectifAtteint(ludophile, map, but)){
            deplacement(ludophile, map);
        }
    }


    /* ================ */
    /* Création de Type */
    /* ================ */

    //Création de type : Joueur
    Joueur newJoueur(){
        Joueur ludophile = new Joueur();
        ludophile.nbVie = nbVie;
        ludophile.nbReussite = nbReussite;
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
        but.idxObjectifLigne = (int)(random()*length(map, 1)/2);
        but.idxObjectifColonne = (int)(random()*length(map, 2)/2);

        map[but.idxObjectifLigne][but.idxObjectifColonne] = but.DRAPEAU;
    }

    //Placement du joueur dans la map
    void placementJoueur(String[][] map, Joueur ludophile){
        map[length(map, 1)-1][length(map, 2)-1] = ludophile.personnage;
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



    /* ============================================ */
    /* Déplacement, boucle et algortithme du Joueur */
    /* ============================================ */

    //Vérification du déplacement vers le Nord
    boolean deplacementPossibleNord(Joueur ludophile, String[][] map){
        if(coordonnéeLigne(ludophile, map) == 0){
            return false;
        }

        if(equals(map[coordonnéeLigne(ludophile, map)-1][coordonnéeColonne(ludophile, map)], MONTAGNE)){
            return false;
        }

        return true;
    }

    void testDeplacementPossibleNord(){
        Joueur ludophile = newJoueur();
        ludophile.personnage = "👨";

        String[][] map = new String[][]{{CHEMIN,ludophile.personnage,CHEMIN},
                                        {CHEMIN,CHEMIN,CHEMIN},
                                        {CHEMIN,CHEMIN,CHEMIN}};
        assertFalse(deplacementPossibleNord(ludophile, map));

        map = new String[][]{{CHEMIN,MONTAGNE,CHEMIN},
                             {CHEMIN,ludophile.personnage,CHEMIN},
                             {CHEMIN,CHEMIN,CHEMIN}};
        assertFalse(deplacementPossibleNord(ludophile, map));
        
        map = new String[][]{{CHEMIN,CHEMIN,CHEMIN},
                             {CHEMIN,ludophile.personnage,CHEMIN},
                             {CHEMIN,CHEMIN,CHEMIN}};
        assertTrue(deplacementPossibleNord(ludophile, map));
    }

    //Vérification du déplacement vers le Sud
    boolean deplacementPossibleSud(Joueur ludophile, String[][] map){
        if(coordonnéeLigne(ludophile, map) == (length(map, 1)-1)){
            return false;
        }

        if(equals(map[coordonnéeLigne(ludophile, map)+1][coordonnéeColonne(ludophile, map)], MONTAGNE)){
            return false;
        }

        return true;
    }

    void testDeplacementPossibleSud(){
        Joueur ludophile = newJoueur();
        ludophile.personnage = "👨";

        String[][] map = new String[][]{{CHEMIN,CHEMIN,CHEMIN},
                                        {CHEMIN,CHEMIN,CHEMIN},
                                        {CHEMIN,ludophile.personnage,CHEMIN}};
        assertFalse(deplacementPossibleSud(ludophile, map));

        map = new String[][]{{CHEMIN,CHEMIN,CHEMIN},
                             {CHEMIN,ludophile.personnage,CHEMIN},
                             {CHEMIN,MONTAGNE,CHEMIN}};
        assertFalse(deplacementPossibleSud(ludophile, map));
        
        map = new String[][]{{CHEMIN,CHEMIN,CHEMIN},
                             {CHEMIN,ludophile.personnage,CHEMIN},
                             {CHEMIN,CHEMIN,CHEMIN}};
        assertTrue(deplacementPossibleSud(ludophile, map));
    }

    //Vérification du déplacement vers l'Ouest
    boolean deplacementPossibleOuest(Joueur ludophile, String[][] map){
        if(coordonnéeColonne(ludophile, map) == 0){
            return false;
        }

        if(equals(map[coordonnéeLigne(ludophile, map)][coordonnéeColonne(ludophile, map)-1], MONTAGNE)){
            return false;
        }

        return true;
    }

    void testDeplacementPossibleOuest(){
        Joueur ludophile = newJoueur();
        ludophile.personnage = "👨";

        String[][] map = new String[][]{{CHEMIN,CHEMIN,CHEMIN},
                                        {CHEMIN,CHEMIN,CHEMIN},
                                        {ludophile.personnage,CHEMIN,CHEMIN}};
        assertFalse(deplacementPossibleOuest(ludophile, map));

        map = new String[][]{{CHEMIN,CHEMIN,CHEMIN},
                             {MONTAGNE,ludophile.personnage,CHEMIN},
                             {CHEMIN,CHEMIN,CHEMIN}};
        assertFalse(deplacementPossibleOuest(ludophile, map));
        
        map = new String[][]{{CHEMIN,CHEMIN,CHEMIN},
                             {CHEMIN,ludophile.personnage,CHEMIN},
                             {CHEMIN,CHEMIN,CHEMIN}};
        assertTrue(deplacementPossibleOuest(ludophile, map));
    }

    //Vérification du déplacement vers l'Est
    boolean deplacementPossibleEst(Joueur ludophile, String[][] map){
        if(coordonnéeColonne(ludophile, map) == (length(map, 2)-1)){
            return false;
        }

        if(equals(map[coordonnéeLigne(ludophile, map)][coordonnéeColonne(ludophile, map)+1], MONTAGNE)){
            return false;
        }

        return true;
    }

    void testDeplacementPossibleEst(){
        Joueur ludophile = newJoueur();
        ludophile.personnage = "👨";

        String[][] map = new String[][]{{CHEMIN,CHEMIN,CHEMIN},
                                        {CHEMIN,CHEMIN,ludophile.personnage},
                                        {CHEMIN,CHEMIN,CHEMIN}};
        assertFalse(deplacementPossibleEst(ludophile, map));

        map = new String[][]{{CHEMIN,CHEMIN,CHEMIN},
                             {CHEMIN,ludophile.personnage,MONTAGNE},
                             {CHEMIN,CHEMIN,CHEMIN}};
        assertFalse(deplacementPossibleEst(ludophile, map));
        
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
            map[coordonnéeLigne(ludophile, map)-1][coordonnéeColonne(ludophile, map)] = ludophile.personnage;
            map[coordonnéeLigne(ludophile, map)+1][coordonnéeColonne(ludophile, map)] = CHEMIN;
            afficherMap(map, ludophile);
            println();
        } 
    }

    //Avancer vers le Sud
    void avancerSud(Joueur ludophile, String[][] map){
        if(!deplacementPossibleSud(ludophile, map)){
            kaomijiOrateurln(ROUGE + "Ce déplacement n'est pas possible !" + RESET);
        } else {
            map[coordonnéeLigne(ludophile, map)+1][coordonnéeColonne(ludophile, map)] = ludophile.personnage;
            map[coordonnéeLigne(ludophile, map)][coordonnéeColonne(ludophile, map)] = CHEMIN;
            afficherMap(map, ludophile);
            println();
        }
        
    }

    //Avancer vers l'Est
    void avancerEst(Joueur ludophile, String[][] map){
        if(!deplacementPossibleEst(ludophile, map)){
            kaomijiOrateurln(ROUGE + "Ce déplacement n'est pas possible !" + RESET);
        } else {
            map[coordonnéeLigne(ludophile, map)][coordonnéeColonne(ludophile, map)+1] = ludophile.personnage;
            map[coordonnéeLigne(ludophile, map)][coordonnéeColonne(ludophile, map)] = CHEMIN;
            afficherMap(map, ludophile);
            println();
        }
    }

    //Avancer vers l'Ouest
    void avancerOuest(Joueur ludophile, String[][] map){
        if(!deplacementPossibleOuest(ludophile, map)){
            kaomijiOrateurln(ROUGE + "Ce déplacement n'est pas possible !" + RESET);
        } else {
            map[coordonnéeLigne(ludophile, map)][coordonnéeColonne(ludophile, map)-1] = ludophile.personnage;
            map[coordonnéeLigne(ludophile, map)][coordonnéeColonne(ludophile, map)+1] = CHEMIN;
            afficherMap(map, ludophile);
            println();
        }
    }

    //Choix de déplacement pour les boucles
    void choixDeplacementBoucle(int nbChoix, int nbCases, Joueur ludophile, String[][] map){
        if(nbChoix == 8){
            for(int cpt=0; cpt<nbCases; cpt++){
                if(!deplacementPossibleNord(ludophile, map)){
                    cpt = nbCases;
                }
                avancerNord(ludophile, map);
                delay(500);
            }
        } else if(nbChoix == 6){
            for(int cpt=0; cpt<nbCases; cpt++){
                if(!deplacementPossibleEst(ludophile, map)){
                    cpt = nbCases;
                }
                avancerEst(ludophile, map);
                delay(500);
            }
        } else if(nbChoix == 4){
            for(int cpt=0; cpt<nbCases; cpt++){
                if(!deplacementPossibleOuest(ludophile, map)){
                    cpt = nbCases;
                }
                avancerOuest(ludophile, map);
                delay(500);
            }
        } else if(nbChoix == 2){
            for(int cpt=0; cpt<nbCases; cpt++){
                if(!deplacementPossibleSud(ludophile, map)){
                    cpt = nbCases;
                }
                avancerSud(ludophile, map);
                delay(500);
            }
        } else {
            kaomijiOrateurln(JAUNE + "Tu ne t'es pas déplacé. Assure-toi d'appuyer sur le bon bouton pour te déplacer !" + RESET);
        }
    }

    //Déplacement en boucle selon le choix du Joueur
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

    //Choix de déplacement global
    void deplacement(Joueur ludophile, String[][] map){
        String saisie;
        int choix;

        kaomijiOrateur("Choisis un chiffre parmi ceux proposés, qui correspond au déplacement que tu veux faire !\n" + 
                        espacement(maitreKaomiji(nbVie) + " - ") + "(8):⬆️   ; (6):➡️   ; (4):⬅️   ; (2):⬇️   ; (1):🔁\nChoix : ");
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
        } else {
            kaomijiOrateurln(JAUNE + "Tu ne t'es pas déplacé. Assure-toi d'appuyer sur le bon bouton pour te déplacer !" + RESET);
        }
    }



    /* ================================================================== */
    /* Tout ce qui est relatif à la création et information du personnage */
    /* ================================================================== */

    //Confirmera ou non le fait que map[idxL][idxC] contient le joueur
    boolean estPersonnage(Joueur ludophile, String[][] map, int idxL, int idxC){
        return equals(map[idxL][idxC], ludophile.personnage);
    }

    void testEstPersonnage(){
        Joueur ludophile = newJoueur();
        ludophile.personnage = "👨";
        String[][] map = new String[][]{{CHEMIN, CHEMIN, CHEMIN},
                                        {CHEMIN, ludophile.personnage, CHEMIN},
                                        {CHEMIN, CHEMIN, CHEMIN}};

        assertTrue(estPersonnage(ludophile, map, 1,1));
        assertFalse(estPersonnage(ludophile, map, 0, 0));
    }

    //Coordonnées du joueur sur l'axe X
    int coordonnéeLigne(Joueur ludophile, String[][] map){
        int idxL = 0;
        int idxC = 0;

        while(!estPersonnage(ludophile, map, idxL, idxC)){
            idxC++;
            if(idxC>(length(map, 2)-1)){
                idxL++;
                idxC = 0;
            }
        }

        return idxL;
    }

    void testCoordonnéeLigne(){
        Joueur ludophile = newJoueur();
        ludophile.personnage = "👨";

        String[][] map = new String[][]{{CHEMIN, CHEMIN, CHEMIN},
                                        {CHEMIN, ludophile.personnage, CHEMIN},
                                        {CHEMIN, CHEMIN, CHEMIN}};
        assertEquals(1, coordonnéeLigne(ludophile, map));

        map = new String[][]{{CHEMIN, ludophile.personnage, CHEMIN},
                             {CHEMIN, CHEMIN, CHEMIN},
                             {CHEMIN, CHEMIN, CHEMIN}};
        assertEquals(0, coordonnéeLigne(ludophile, map));
    }

    //Coordonnées du joueur sur l'axe Y
    int coordonnéeColonne(Joueur ludophile, String[][] map){
        int idxL = 0;
        int idxC = 0;

        while(!estPersonnage(ludophile, map, idxL, idxC)){
            idxC++;
            if(idxC>(length(map, 2)-1)){
                idxL++;
                idxC = 0;
            }
        }

        return idxC;
    }

    void testCoordonnéeColonne(){
        Joueur ludophile = newJoueur();
        ludophile.personnage = "👨";

        String[][] map = new String[][]{{CHEMIN, CHEMIN, CHEMIN},
                                        {CHEMIN, ludophile.personnage, CHEMIN},
                                        {CHEMIN, CHEMIN, CHEMIN}};
        assertEquals(1, coordonnéeColonne(ludophile, map));

        map = new String[][]{{CHEMIN, ludophile.personnage, CHEMIN},
                             {CHEMIN, CHEMIN, CHEMIN},
                             {CHEMIN, CHEMIN, CHEMIN}};
        assertEquals(1, coordonnéeColonne(ludophile, map));
    }

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
        ludophile.position = "[" + (coordonnéeLigne(ludophile, map)+1) + ";" + (coordonnéeColonne(ludophile, map)+1) + "]";
        return ludophile.position;
    }

    void testPositionJoueur(){
        Joueur ludophile = newJoueur();
        ludophile.personnage = "👨";

        String[][] map = new String[][]{{CHEMIN, CHEMIN, CHEMIN},
                                        {CHEMIN, ludophile.personnage, CHEMIN},
                                        {CHEMIN, CHEMIN, CHEMIN}};
        assertEquals("[2;2]",positionJoueur(ludophile, map));

        map = new String[][]{{ludophile.personnage, CHEMIN, CHEMIN},
                             {CHEMIN, CHEMIN, CHEMIN},
                             {CHEMIN, CHEMIN, CHEMIN}};
        assertEquals("[1;1]",positionJoueur(ludophile, map));
    }

    //Affichage des informations (Nom, PV, Coordonnées, Reussite)
    void informationJoueur(Joueur ludophile, String[][] map){
        print(GRAS + ludophile.nom + " - PV: " + nbVie + " ; Coordonées: " + positionJoueur(ludophile, map) + " ; Nombre de Reussite: " + nbReussite + RESET);
    }



    /* ======================================================= */
    /* Tout ce qui est relatif au effet des éléments de la map */
    /* ======================================================= */

    //Retournera vrai si le Joueur a atteint l'objectif
    boolean objectifAtteint(Joueur ludophile, String[][] map, Objectif but){
        if(equals(map[but.idxObjectifLigne][but.idxObjectifColonne], ludophile.personnage)){
            return true;
        }
        return false;
    }

    void testObjectifAtteint(){
        Joueur ludophile = newJoueur();
        Objectif but = newObjectif();

        ludophile.personnage = "👨";

        String[][] map = new String[][]{{CHEMIN,CHEMIN,CHEMIN},
                                        {ludophile.personnage, CHEMIN, CHEMIN},
                                        {CHEMIN,CHEMIN,CHEMIN}};


        but.idxObjectifLigne = 1;
        but.idxObjectifColonne = 0;
        assertTrue(objectifAtteint(ludophile, map, but));

        but.idxObjectifLigne = 0;
        but.idxObjectifColonne = 0;
        assertFalse(objectifAtteint(ludophile, map, but));
    }



    /* ===================================================== */
    /* Tout ce qui concerne begin() lors du lancement du jeu */
    /* ===================================================== */
    void begin(Joueur ludophile, Objectif but){
        creationPersonnage(ludophile);
        tutoriel(ludophile, but);
        delay(1000);
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
        kaomijiOrateurln("Tu es donc du genre " + ludophile.genre + " !");
        personnageJoueur(ludophile);
        delay(1000);
        recaputilatif(ludophile);
        delay(1000);
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
        }
    }



    /* ======================================================== */
    /* Tout ce qui concerne tutoriel() lors du lancement du jeu */
    /* ======================================================== */

    //Tutoriel pour avancer
    void avancerTutoriel(Joueur ludophile, Objectif but, String[][] map){
        remplissageMap(map);
        but.idxObjectifLigne = length(map,1)/2;
        but.idxObjectifColonne = length(map,2)/2;
        map[but.idxObjectifLigne][but.idxObjectifColonne] = but.DRAPEAU;
        map[length(map,1)-1][length(map,2)/2] = ludophile.personnage;

        kaomijiOrateurln("On va commencer doucement. Avance jusqu'à atteindre le drapeau rouge !");
        kaomijiOrateurln("Pour t'entraîner, appuie sur la touche (8) du clavier pour avancer !");
        afficherMap(map, ludophile);
        println();

        while(!objectifAtteint(ludophile, map, but)){
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
        map[but.idxObjectifLigne][but.idxObjectifColonne] = but.DRAPEAU;
        map[length(map,1)-1][length(map,2)/2] = ludophile.personnage;

        kaomijiOrateurln("Déplace-toi jusqu'à ce que tu atteignes le drapeau rouge !");
        kaomijiOrateurln("Pour t'entraîner, appuie sur la touche (8) et (6) du clavier pour te déplacer!");
        afficherMap(map, ludophile);
        println();

        while(!objectifAtteint(ludophile, map, but)){
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
        map[but.idxObjectifLigne][but.idxObjectifColonne] = but.DRAPEAU;
        map[length(map,1)-1][length(map,2)/2] = ludophile.personnage;

        kaomijiOrateurln("Déplace-toi jusqu'à ce que tu atteignes le drapeau rouge !");
        kaomijiOrateurln("Pour t'entraîner, appuie sur la touche (8), (6) et (4) du clavier pour te déplacer!");
        afficherMap(map, ludophile);
        println();

        while(!objectifAtteint(ludophile, map, but)){
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
        map[but.idxObjectifLigne][but.idxObjectifColonne] = but.DRAPEAU;
        map[0][0] = ludophile.personnage;

        kaomijiOrateurln("Déplace-toi jusqu'à ce que tu atteignes le drapeau rouge !");
        kaomijiOrateurln("Pour t'entraîner, appuie sur la touche (8), (6), (4) et (2) du clavier pour te déplacer!");
        afficherMap(map, ludophile);
        println();

        while(!objectifAtteint(ludophile, map, but)){
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
        map[but.idxObjectifLigne][but.idxObjectifColonne] = but.DRAPEAU;
        map[0][0] = ludophile.personnage;

        kaomijiOrateurln("Déplace-toi jusqu'à ce que tu atteignes le drapeau rouge !");
        kaomijiOrateurln("Pour t'entraîner, appuie sur la touche (1) du clavier pour te déplacer!");
        afficherMap(map, ludophile);
        println();

        while(!objectifAtteint(ludophile, map, but)){
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



    /* ============================================================ */
    /* Tout ce qui booléen pour les boucles while() et alternatives */
    /* ============================================================ */
}