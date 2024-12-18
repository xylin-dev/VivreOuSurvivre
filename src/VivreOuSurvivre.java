class VivreOuSurvivre extends Program{
    //Variable Globale
    final String CHEMIN = "⬛";
    final String ARBRE = "🌳";
    final String MONTAGNE = "🗻";
    final String BOMBE = "💣";
    final String EXPLOSION = "💥";
    final String LAVE = "🔥";
    final String CARTE = "🎴";
    final String OBJECTIF = "🚩";

    int nbVie = 10;
    int nbViePrecedent = 10;

    int nbReussite = 0;

    // Algorithme principale
    void algorithm(){
        Joueur ludophile = newJoueur();
        creationPersonnage(ludophile);
        String[][] carte = new String[20][20];

        /*initialisationMap(carte, ludophile);
        afficherMap(carte, ludophile);
        for(int i=0; i<10; i++){
            deplacementPersonnage(ludophile, carte);
        }*/
    }

    //Remplira la carte de chemin
    void remplissageMap(String[][] map){
        for(int idxI=0; idxI<length(map,1); idxI++){
            for(int idxJ=0; idxJ<length(map,2); idxJ++){
                map[idxI][idxJ] = CHEMIN;
            }
        }
    }

    //Ajoutera des élements dans la carte aléatoirement
    void elementMap(String[][] map, String[] tab, double probabilite){
        double probabiliteAleatoire = random();
        int idxAleatoire = (int) (random()*length(tab));
        int idxLigneAleatoire = (int)(random()*length(map, 1));
        int idxColonneAleatoire = (int)(random()*length(map, 2));

        if(probabiliteAleatoire>probabilite){
            map[idxLigneAleatoire][idxColonneAleatoire] = tab[idxAleatoire];
        }
    }

    //Placera aléatoirement l'objectif du joueur
    void objectifMap(String[][] map){
        int idxLigneAleatoire = (int)(random()*length(map, 1)/2);
        int idxColonneAleatoire = (int)(random()*length(map, 2)/2);

        map[idxLigneAleatoire][idxColonneAleatoire] = OBJECTIF;
    }

    //Placera le joueur dans la map
    void placementJoueur(String[][] map, Joueur ludophile){
        map[length(map, 1)-1][length(map, 2)-1] = ludophile.personnage;
    }
    
    //Initialisera la carte
    void initialisationMap(String[][] map, Joueur ludophile){
        String[] element = new String[]{ARBRE, MONTAGNE, BOMBE, LAVE, CARTE};
        double probabilite = 0.3;

        remplissageMap(map);

        for(int cpt=0; cpt<(length(map, 1)*length(map,2))/3; cpt++){
            elementMap(map, element, probabilite);
        }

        objectifMap(map);
        placementJoueur(map, ludophile);
    }

    //Affichera la carte
    void afficherMap(String[][] map, Joueur ludophile){
        for(int idxI=0; idxI<length(map,1); idxI++){
            for(int idxJ=0; idxJ<length(map,2); idxJ++){
                print(map[idxI][idxJ]);
            }
            println();
        }
        
        informationJoueur(ludophile, map);
    }

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
            kaomijiOrateur("Ton choix n'est pas bon, essaie encore : ");
            saisie = readString();
        }
        
        return stringtoInt(saisie);
    }

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
    }

    //Création de type : Joueur
    Joueur newJoueur(){
        Joueur ludophile = new Joueur();
        ludophile.nbVie = nbVie;
        ludophile.nbReussite = nbReussite;
        return ludophile;
    }

    //Confirmera ou non le fait que map[idxL][idxC] contient le joueur
    boolean estPersonnage(Joueur ludophile, String[][] map, int idxL, int idxC){
        return equals(map[idxL][idxC], ludophile.personnage);
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

    //Avancer vers le Nord
    void avancerNord(Joueur ludophile, String[][] map){
        if(!deplacementPossibleNord(ludophile, map)){
            println(maitreKaomiji(nbVie) + " - Ce déplacement n'est pas possible, vous risquez de sortir de la carte !");
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
            println(maitreKaomiji(nbVie) + " - Ce déplacement n'est pas possible, vous risquez de sortir de la carte !");
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
            println(maitreKaomiji(nbVie) + " - Ce déplacement n'est pas possible, vous risquez de sortir de la carte !");
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
            println(maitreKaomiji(nbVie) + " - Ce déplacement n'est pas possible, vous risquez de sortir de la carte !");
        } else {
            map[coordonnéeLigne(ludophile, map)][coordonnéeColonne(ludophile, map)-1] = ludophile.personnage;
            map[coordonnéeLigne(ludophile, map)][coordonnéeColonne(ludophile, map)+1] = CHEMIN;
            afficherMap(map, ludophile);
            println();
        }
    }

    //Avancer vers n direction n fois (boucle)
    /*void avancerBoucle(Joueur ludophile, String[][] map){
        print(maitreKaomiji(nbVie) + " - Voici les déplacements en boucle possibles :\n" + 
                                     "1 : Avancer vers le Nord\n" +
                                     "2 : Avancer vers le Sud\n" +
                                     "3 : Avancer vers l'Est\n" +
                                     "4 : Avancer vers l'Ouest\n" +
                                     "Que choisissez-vous ? "
        );

        int choix = readInt();

        while(choix>4 || choix<1){
            print(maitreKaomiji(nbVie) + " - Vous devez choisir un chiffre qui correspond à un déplacement parmi ceux mentionnés ci-dessus : ");
            choix = readInt();
        }

        print(maitreKaomiji(nbVie) + " - Combien de cases voulez-vous avancer ? Entrez un chiffre : ");
        int nbCases = readInt();

        if(choix == 1){
            for(int cpt=0; cpt<nbCases; cpt++){
                avancerNord(ludophile, map);
                println();
                delay(500);
            }
        } else if(choix == 2){
            for(int cpt=0; cpt<nbCases; cpt++){
                avancerSud(ludophile, map);
                println();
                delay(500);
            }
        } else if(choix == 3){
            for(int cpt=0; cpt<nbCases; cpt++){
                avancerEst(ludophile, map);
                println();
                delay(500);
            }
        } else if(choix == 4){
            for(int cpt=0; cpt<nbCases; cpt++){
                avancerOuest(ludophile, map);
                println();
                delay(500);
            }
        }
    }*/

    //Déplacement du personnage
    /*void deplacementPersonnage(Joueur ludophile, String[][] map){
        print(maitreKaomiji(nbVie) + " - Voici les déplacements possibles :\n" + 
                                     "1 : Avancer vers le Nord\n" +
                                     "2 : Avancer vers le Sud\n" +
                                     "3 : Avancer vers l'Est\n" +
                                     "4 : Avancer vers l'Ouest\n" +
                                     "5 : Avancer en Boucle\n" +
                                     "Que choisissez-vous ? "
        );

        int choix = readInt();

        while(choix>5 || choix<1){
            print(maitreKaomiji(nbVie) + " - Vous devez choisir un chiffre qui correspond à un déplacement parmi ceux mentionnés ci-dessus : ");
            choix = readInt();
        }

        if(choix == 1){
            avancerNord(ludophile, map);
        } else if(choix == 2){
            avancerSud(ludophile, map);
        } else if(choix == 3){
            avancerEst(ludophile, map);
        } else if(choix == 4){
            avancerOuest(ludophile, map);
        } else if(choix == 5){
            avancerBoucle(ludophile, map);
        }
    }*/

    //Introduction et création du personnage lors du démarrage du jeu
    void creationPersonnage(Joueur ludophile){
        String choix;

        kaomijiOrateurln("Bienvenue dans VivreOuSurvivre ! Dans ce jeu, tu vas apprendre les bases des algorithmes en t'amusant.");
        delay(1000);
        kaomijiOrateur("Je me présente, je suis le maître du jeu : Kaomiji, ton super compagnon ! Et toi, qui es-tu ? ");
        ludophile.nom = readString();
        delay(1000);
        kaomijiOrateurln(ludophile.nom + "? Super ton nom ! Avant de commencer à t'apprendre les bases des algorithmes, il faut d'abord créer ton personnage.");
        delay(1000);
        genreJoueur(ludophile);
        delay(1000);
        kaomijiOrateurln("Tu es donc du genre " + ludophile.genre + " !");
        delay(1000);
        personnageJoueur(ludophile);
        delay(1000);
        recaputilatif(ludophile);
        delay(1000);
        kaomijiOrateur("Souhaitez-vous passer un tutoriel? [Oui; Non] : ");
        choix = readString();
        if(equals(choix, "Oui")){
            delay(1000);
            tutoriel(ludophile);
        }
    }

    //Genre du Joueur
    String genreJoueur(Joueur ludophile){
        kaomijiOrateur("Quel est votre genre [Masculin (M); Feminin (F)] : ");
        ludophile.genre = readString();
        while((!equals(ludophile.genre, "Masculin") && !equals(ludophile.genre, "M")) && (!equals(ludophile.genre, "Feminin") && !equals(ludophile.genre, "F"))){
            kaomijiOrateur("Non, vous devez choisir entre Masculin (ou M) et Feminin (ou F) : ");
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
        delay(500);
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
            kaomijiOrateur("Ton choix n'est pas bon, essaie encore : ");
            saisie = readString();
            choix = verificationString(saisie);
        }

        return choix-1;
    }

    //Espacement dans le texte (principalement pour Kaomiji)
    String espacement(String mot){
        String espace = "";

        for(int cpt=0; cpt<length(mot); cpt++){
            espace = espace + " ";
        }

        return espace;
    }

    //Récaputilif de la création du personnage
    void recaputilatif(Joueur ludophile){
        kaomijiOrateurln("Voici un récapitulatif de ce que tu m'as donné : ");
        delay(500);
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

    //Affichage des informations (Nom, PV, Coordonnées, Reussite)
    void informationJoueur(Joueur ludophile, String[][] map){
        print(ludophile.nom + " - PV: " + nbVie + " ; Coordonées: " + positionJoueur(ludophile, map) + " ; Nombre de Reussite: " + nbReussite);
    }

    //Tutoriel Global
    void tutoriel(Joueur ludophile){
        String[][] map = new String[5][5];
        avancerTutoriel(ludophile, map);
        delay(1000);
        droiteTutoriel(ludophile, map);
        delay(1000);
        gaucheTutoriel(ludophile, map);
        delay(1000);
        basTutoriel(ludophile, map);
        delay(1000);
    }

    //Tutoriel pour avancer
    void avancerTutoriel(Joueur ludophile, String[][] map){
        remplissageMap(map);
        map[length(map,1)/2][length(map,2)/2] = OBJECTIF;
        map[length(map,1)-1][length(map,2)/2] = ludophile.personnage;

        kaomijiOrateurln("On va commencer doucement. Avance jusqu'à atteindre le drapeau rouge !");
        delay(1000);
        kaomijiOrateurln("Pour t'entraîner, appuie sur la touche (8) du clavier pour avancer !");
        delay(1000);
        afficherMap(map, ludophile);
        println();

        while(!equals(map[length(map,1)/2][length(map,2)/2], ludophile.personnage)){
            print("(8):⬆️ \nChoix : ");
            String saisie = readString();
            int choix = verificationString(saisie);
            if(choix == 8){
                avancerNord(ludophile, map);
            }else{
                kaomijiOrateurln("Tu ne t'es pas déplacé. Assure-toi d'appuyer sur le bon bouton pour te déplacer !");
            }
        }

        kaomijiOrateurln("Félicitations ! Maintenant, on passe aux déplacements vers la droite.");
    }

    //Tutoriel pour déplacement droite
    void droiteTutoriel(Joueur ludophile, String[][] map){
        remplissageMap(map);
        map[0][length(map,2)-1] = OBJECTIF;
        map[length(map,1)-1][length(map,2)/2] = ludophile.personnage;

        kaomijiOrateurln("Maintenant, déplace-toi jusqu'à atteindre le drapeau rouge !");
        delay(1000);
        kaomijiOrateurln("Pour t'entraîner, appuie sur la touche (8) et (6) du clavier pour te déplacer!");
        delay(1000);
        afficherMap(map, ludophile);
        println();

        while(!equals(map[0][length(map,2)-1], ludophile.personnage)){
            print("(8):⬆️   ; (6):➡️\nChoix : ");
            String saisie = readString();
            int choix = verificationString(saisie);
            if(choix == 8){
                avancerNord(ludophile, map);
            }else if(choix == 6){
                avancerEst(ludophile, map);
            }else{
                kaomijiOrateurln("Tu ne t'es pas déplacé. Assure-toi d'appuyer sur le bon bouton pour te déplacer !");
            }
        }

        kaomijiOrateurln("Félicitations ! Maintenant, on passe aux déplacements vers la gauche.");
    }

    //Tutoriel déplacement vers la gauche
    void gaucheTutoriel(Joueur ludophile, String[][] map){
        remplissageMap(map);
        map[0][0] = OBJECTIF;
        map[length(map,1)-1][length(map,2)/2] = ludophile.personnage;

        kaomijiOrateurln("Maintenant, déplace-toi jusqu'à atteindre le drapeau rouge !");
        delay(1000);
        kaomijiOrateurln("Pour t'entraîner, appuie sur la touche (8), (6) et (4) du clavier pour te déplacer!");
        delay(1000);
        afficherMap(map, ludophile);
        println();

        while(!equals(map[0][0], ludophile.personnage)){
            print("(8):⬆️   ; (6):➡️   ; (4)⬅️\nChoix : ");
            String saisie = readString();
            int choix = verificationString(saisie);
            if(choix == 8){
                avancerNord(ludophile, map);
            }else if(choix == 6){
                avancerEst(ludophile, map);
            }else if(choix == 4){
                avancerOuest(ludophile, map);
            }else{
                kaomijiOrateurln("Tu ne t'es pas déplacé. Assure-toi d'appuyer sur le bon bouton pour te déplacer !");
            }
        }

        kaomijiOrateurln("Félicitations ! Maintenant, on passe aux déplacements vers le bas.");
    }

    //Tutoriel déplacement vers le bas
    void basTutoriel(Joueur ludophile, String[][] map){
        remplissageMap(map);
        map[length(map,1)-1][length(map,2)-1] = OBJECTIF;
        map[0][0] = ludophile.personnage;

        kaomijiOrateurln("Maintenant, déplace-toi jusqu'à atteindre le drapeau rouge !");
        delay(1000);
        kaomijiOrateurln("Pour t'entraîner, appuie sur la touche (8), (6), (4) et (2) du clavier pour te déplacer!");
        delay(1000);
        afficherMap(map, ludophile);
        println();

        while(!equals(map[length(map,1)-1][length(map,2)-1], ludophile.personnage)){
            print("(8):⬆️   ; (6):➡️   ; (4)⬅️   ; (2)⬇️\nChoix : ");
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
                kaomijiOrateurln("Tu ne t'es pas déplacé. Assure-toi d'appuyer sur le bon bouton pour te déplacer !");
            }
        }

        kaomijiOrateurln("Félicitations ! Maintenant, on passe aux boucles.");
    }    
}