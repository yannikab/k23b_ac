package k23b.sa;

import org.apache.log4j.PropertyConfigurator;

import k23b.sa.Threads.MainThread;

/**
 * The application for the Software Agent
 *
 */
public class App {

    public static void main(String[] args) {

        PropertyConfigurator.configure("log4j.properties");

        new MainThread().start();
    }
}
