package com.codehistorian;

// java -jar refuses to launch a class that directly extends Application without --module-path.
public class Launcher {

    public static void main(String[] args) {
        Main.main(args);
    }
}
