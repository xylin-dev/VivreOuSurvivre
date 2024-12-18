class VivreOuSurvivre extends Program{
    //Variable Globale
    final String CHEMIN = "⬛";
    final String ARBRE = "🌳";
    final String MONTAGNE = "🗻";
    final String BOMBE = "💣";
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
        initialisationCarte(carte, ludophile);
        afficherCarte(carte, ludophile);
        for(int i=0; i<10; i++){
            deplacementPersonnage(ludophile, carte);
        }
        
    }

    //Remplira la carte de chemin
    void remplissageCarte(String[][] map){
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
    void objectifCarte(String[][] map){
        int idxLigneAleatoire = (int)(random()*length(map, 1)/2);
        int idxColonneAleatoire = (int)(random()*length(map, 2)/2);

        map[idxLigneAleatoire][idxColonneAleatoire] = OBJECTIF;
    }

    //Placera le joueur dans la map
    void placementJoueur(String[][] map, Joueur ludophile){
        int idxLigneAleatoire = (int)(random()*length(map, 1));
        int idxColonneAleatoire = (int)(random()*length(map, 2));

        if(equals(map[length(map, 1)-1][length(map, 2)-2], OBJECTIF)){
            map[idxLigneAleatoire][idxColonneAleatoire] = ludophile.personnage;
        } else {
            map[length(map, 1)-1][length(map, 2)-1] = ludophile.personnage;
        }
    }
    
    //Initialisera la carte
    void initialisationCarte(String[][] map, Joueur ludophile){
        String[] element = new String[]{ARBRE, MONTAGNE, BOMBE, LAVE, CARTE};
        double probabilite = 0.3;

        remplissageCarte(map);

        for(int cpt=0; cpt<(length(map, 1)*length(map,2))/3; cpt++){
            elementMap(map, element, probabilite);
        }

        objectifCarte(map);
        placementJoueur(map, ludophile);
    }

    //Affichera la carte
    void afficherCarte(String[][] map, Joueur ludophile){
        for(int idxI=0; idxI<length(map,1); idxI++){
            for(int idxJ=0; idxJ<length(map,2); idxJ++){
                print(map[idxI][idxJ]);
            }
            println();
        }
        
        informationJoueur(ludophile, map);
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

    //Facilitera les moments où Kaomiji parle (au lieu d'utiliser println())
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

    //Recherchera le personnage dans la map
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
            afficherCarte(map, ludophile);
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
            afficherCarte(map, ludophile);
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
            afficherCarte(map, ludophile);
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
            afficherCarte(map, ludophile);
            println();
        }
    }

    //Avancer vers n direction n fois (boucle)
    void avancerBoucle(Joueur ludophile, String[][] map){
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
    }

    //Déplacement du personnage
    void deplacementPersonnage(Joueur ludophile, String[][] map){
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
    }

    //Explication de VivreOuSurvivre lors du démarrage du jeu
    void creationPersonnage(Joueur ludophile){
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
        int choix = readInt();
        while(choix>length(personnage) || choix<1){
            kaomijiOrateur("Ton choix n'est pas bon, essaie encore : ");
            choix = readInt();
        }

        return choix-1;
    }

    //Espacement dans le texte
    String espacement(String mot){
        String espace = "";

        for(int cpt=0; cpt<length(mot); cpt++){
            espace = espace + " ";
        }

        return espace;
    }

    //Récaputilif de la création de personnage
    void recaputilatif(Joueur ludophile){
        kaomijiOrateurln("Voici un récapitulatif de ce que tu m'as donné : ");
        delay(500);
        print(espacement(maitreKaomiji(nbVie) + " - ") + "Ton nom est : " + ludophile.nom + "\n");
        delay(500);
        print(espacement(maitreKaomiji(nbVie) + " - ") + "Ton genre est : " + ludophile.genre + "\n");
        delay(500);
        print(espacement(maitreKaomiji(nbVie) + " - ") + "Ton personnage est : " + ludophile.personnage + "\n");
        delay(500);
    }

    //Positionnement du Joueur
    String positionJoueur(Joueur ludophile, String[][] map){
        ludophile.position = "[" + (coordonnéeLigne(ludophile, map)+1) + ";" + (coordonnéeColonne(ludophile, map)+1) + "]";
        return ludophile.position;
    }

    //Affichage des informations (Nom, PV, Coordonnées, Reussite)
    void informationJoueur(Joueur ludophile, String[][] map){
        println(ludophile.nom + " - PV: " + nbVie + " ; Coordonées: " + positionJoueur(ludophile, map) + " ; Nombre de Reussite: " + nbReussite);
    }
}