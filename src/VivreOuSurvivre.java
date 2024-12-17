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
        println(creationPersonnage(ludophile));
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
        int idxLigneAleatoire = (int)(random()*length(map, 1));
        int idxColonneAleatoire = (int)(random()*length(map, 2));

        map[idxLigneAleatoire][idxColonneAleatoire] = OBJECTIF;
    }

    //Placera le joueur dans la map
    void placementJoueur(String[][] map, Joueur ludophile){
        int idxLigneAleatoire = (int)(random()*length(map, 1));
        int idxColonneAleatoire = (int)(random()*length(map, 2));

        if(equals(map[length(map, 1)-1][length(map, 2)-2], OBJECTIF)){
            map[idxLigneAleatoire][idxColonneAleatoire] = ludophile.personnage;
        } else {
            map[length(map, 1)-1][length(map, 2)-2] = ludophile.personnage;
        }
    }
    
    //Initialisera la carte
    void initialisationCarte(String[][] map, Joueur ludophile){
        String[] element = new String[]{ARBRE, MONTAGNE, BOMBE, LAVE, CARTE};
        double probabilite = 0.6;

        remplissageCarte(map);

        for(int cpt=0; cpt<150; cpt++){
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
        String[] kaomiji = new String[]{"( ᵔ ᗜ ᵔ )", "(˶˃ ᵕ ˂˶)", "O_o", "(⌐■-■)", "(ಠ_ಠ)>⌐■-■", "ಠ_ʖಠ", "ರ_ರ", "(ꐦ¬_¬)", "(⪖ ⩋⪕)", "୧(๑•̀ᗝ•́)૭", "(⌐■_■)︻デ═一"};
        int idx = min(length(kaomiji)-1, length(kaomiji) - (nbChances+1));

        if(nbChances<nbViePrecedent){
            idx = min(length(kaomiji)-1, length(kaomiji) - (nbChances+1));
            nbViePrecedent = nbChances;
        }

        return kaomiji[idx];
    }

    void testMaitreKaomiji(){
        int nbChances;

        nbChances = 10;
        assertEquals("( ᵔ ᗜ ᵔ )", maitreKaomiji(nbChances));

        nbChances = 9;
        assertEquals("(˶˃ ᵕ ˂˶)", maitreKaomiji(nbChances));

        nbChances = 8;
        assertEquals("O_o", maitreKaomiji(nbChances));
    }
    

    //Création de type : Joueur
    Joueur newJoueur(){
        Joueur ludophile = new Joueur();
        ludophile.nbVie = nbVie;
        ludophile.nbReussite = nbReussite;
        return ludophile;
    }

    //Nom du Joueur
    String nomJoueur(Joueur ludophile){
        print(maitreKaomiji(nbVie) + " - Quel est votre nom : ");
        ludophile.nom = readString();
        return ludophile.nom;
    }

    //Genre du Joueur
    String genreJoueur(Joueur ludophile){
        print(maitreKaomiji(nbVie) + " - Quel est votre genre [Masculin ; Feminin] : ");
        ludophile.genre = readString();
        while(!equals(ludophile.genre, "Masculin") && !equals(ludophile.genre, "Feminin")){
            print(maitreKaomiji(nbVie) + " - Non, vous devez choisir entre Masculin et Feminin : ");
            ludophile.genre = readString();
        }
        return ludophile.genre;
    }

    //Personnage du Joueur
    String personnageJoueur(Joueur ludophile){
        String[] personnageMasculin = new String[]{"👨","👦","👶"};
        String[] personnageFeminin = new String[]{"👩","👧","👶"};

        if(equals(ludophile.genre, "Masculin")){
            afficherPersonnage(personnageMasculin);
            ludophile.personnage = personnageMasculin[selectionPersonnage(personnageMasculin)];
        } else {
            afficherPersonnage(personnageFeminin);
            ludophile.personnage = personnageFeminin[selectionPersonnage(personnageFeminin)];
        }

        return ludophile.personnage;
    }

    //Affichage des personnages
    void afficherPersonnage(String[] personnage){
        println(maitreKaomiji(nbVie) + " - Voici les personnages qui sont à votre disposition : ");
        for(int idx=0; idx<length(personnage); idx++){
            delay(500);
            println((idx+1) + " : " + personnage[idx]);
        }
    }

    //Selection de personnage
    int selectionPersonnage(String[] personnage){
        print(maitreKaomiji(nbVie) + " - Choisis un personnage en tapant le numéro qui lui correspond : ");
        int choix = readInt();
        while(choix>length(personnage) || choix<1){
            print(maitreKaomiji(nbVie) + " - Ton choix n'est pas bon, essaie encore : ");
            choix = readInt();
        }

        return choix-1;
    }

    //Création de personnage
    String creationPersonnage(Joueur ludophile){
        String nom = nomJoueur(ludophile);
        delay(1000);
        String genre = genreJoueur(ludophile);
        delay(1000);
        String personnage = personnageJoueur(ludophile);
        delay(1000);
        return maitreKaomiji(nbVie) + " - Voici les informations que vous nous avez fournies : \n" +
                                      "Votre nom est : " + nom + "\n" +
                                      "Votre genre est : " + genre + "\n" +
                                      "Vous avez choisi le personnages : " + personnage;
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

    //Positionnement du Joueur
    String positionJoueur(Joueur ludophile, String[][] map){
        ludophile.position = "[" + (coordonnéeLigne(ludophile, map)+1) + ";" + (coordonnéeColonne(ludophile, map)+1) + "]";
        return ludophile.position;
    }

    //Affichage des informations (Nom, PV, Coordonnées, Reussite)
    void informationJoueur(Joueur ludophile, String[][] map){
        println(ludophile.nom + " - PV: " + nbVie + " ; Coordonées: " + positionJoueur(ludophile, map) + " ; Nombre de Reussite: " + nbReussite);
    }

    //Avancer vers le Nord
    void avancerNord(Joueur ludophile, String[][] map){
        print(maitreKaomiji(nbVie) + " - Avancer vers le Nord : combien de fois voulez-vous avancer ? ");
        int nbCases = readInt();

        while(nbCases >= coordonnéeLigne(ludophile, map)){
            print(maitreKaomiji(nbVie) + " - Ce déplacement n'est pas possible, vous risquez de sortir de la carte ! ");
            if(coordonnéeLigne(ludophile, map) == 0){
                print("Nous vous conseillons de changer de direction\n");
                delay(1000);
                deplacementPersonnage(ludophile, map);
            }else{
                print("Essayez encore : ");
                nbCases = readInt();
            }
        }

        for(int cpt=0; cpt<nbCases; cpt++){
            map[coordonnéeLigne(ludophile, map)-1][coordonnéeColonne(ludophile, map)] = ludophile.personnage;
            map[coordonnéeLigne(ludophile, map)+1][coordonnéeColonne(ludophile, map)] = CHEMIN;
            afficherCarte(map, ludophile);
            println();
            delay(1000);
        }
    }

    //Avancer vers le Sud
    void avancerSud(Joueur ludophile, String[][] map){
        print(maitreKaomiji(nbVie) + " - Avancer vers le Sud : combien de fois voulez-vous avancer ? ");
        int nbCases = readInt();

        

        for(int cpt=0; cpt<nbCases; cpt++){
            map[coordonnéeLigne(ludophile, map)+1][coordonnéeColonne(ludophile, map)] = ludophile.personnage;
            map[coordonnéeLigne(ludophile, map)][coordonnéeColonne(ludophile, map)] = CHEMIN;
            afficherCarte(map, ludophile);
            println();
            delay(1000);
        }
    }

    //Avancer vers l'Est
    void avancerEst(Joueur ludophile, String[][] map){
        print(maitreKaomiji(nbVie) + " - Avancer vers l'Est : combien de fois voulez-vous avancer ? ");
        int nbCases = readInt();

        for(int cpt=0; cpt<nbCases; cpt++){
            map[coordonnéeLigne(ludophile, map)][coordonnéeColonne(ludophile, map)+1] = ludophile.personnage;
            map[coordonnéeLigne(ludophile, map)][coordonnéeColonne(ludophile, map)] = CHEMIN;
            afficherCarte(map, ludophile);
            println();
            delay(1000);
        }
    }

    //Avancer vers l'Ouest
    void avancerOuest(Joueur ludophile, String[][] map){
        print(maitreKaomiji(nbVie) + " - Avancer vers l'Ouest : combien de fois voulez-vous avancer ? ");
        int nbCases = readInt();

        for(int cpt=0; cpt<nbCases; cpt++){
            map[coordonnéeLigne(ludophile, map)][coordonnéeColonne(ludophile, map)-1] = ludophile.personnage;
            map[coordonnéeLigne(ludophile, map)][coordonnéeColonne(ludophile, map)+1] = CHEMIN;
            afficherCarte(map, ludophile);
            println();
            delay(1000);
        }
    }

    //Déplacement du personnage
    void deplacementPersonnage(Joueur ludophile, String[][] map){
        print(maitreKaomiji(nbVie) + " - Voici les déplacements possibles : " + "\n" + 
                                     "1 : Avancer vers le Nord" + "\n" +
                                     "2 : Avancer vers le Sud" + "\n" +
                                     "3 : Avancer vers l'Est" + "\n" +
                                     "4 : Avancer vers l'Ouest" + "\n" +
                                     "Que choisissez-vous ? "
        );

        int choix = readInt();

        while(choix>4 || choix<1){
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
        }
    }
}