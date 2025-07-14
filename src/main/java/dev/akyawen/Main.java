package dev.akyawen;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Scanner;

import org.tempura.console.util.Ansi;

import dev.akyawen.util.CustomLogger;
import dev.akyawen.util.Requests;

public class Main {
    private static final Scanner SCANNER = new Scanner(System.in);
    private static final Requests REQUESTS = new Requests();
    private static final CustomLogger LOGGER = new CustomLogger();

    private static String webhookUrl = "None";
    private static String mode = "SPAM + DELETE";
    private static int amount = 10;
    private static int delayMs = 500;
    private static List<String> messages = new ArrayList<String>();

    public static void main(String[] args) {
    	loadConfig("config.ini");
        loadMessages("messages.txt");
        mainMenu();
    }

    private static void mainMenu() {
        while (true) {
            clearScreen();
            printHeader();
            System.out.println(Ansi.CYAN + " Target:      " + Ansi.WHITE + webhookUrl);
            System.out.println(Ansi.CYAN + " Messages:    " + Ansi.WHITE + "messages.txt (" + messages.size() + " lines)");
            System.out.println(Ansi.CYAN + " Mode:        " + Ansi.YELLOW + mode);
            System.out.println(Ansi.CYAN + " Amount:      " + Ansi.WHITE + amount + " messages");
            System.out.println(Ansi.CYAN + " Delay:       " + Ansi.WHITE + delayMs + "ms between messages" + Ansi.SANE);
            System.out.println();
            System.out.println(Ansi.GREEN + " [1] Start Attack");
            System.out.println(" [2] Change Target");
            System.out.println(" [3] Load messages.txt");
            System.out.println(" [4] Edit Mode");
            System.out.println(" [0] Exit" + Ansi.SANE);
            System.out.print("\n>> Select an option: ");
            String input = SCANNER.nextLine();

            switch (input) {
                case "1":
                    startAttack();
                    saveConfig("config.ini");
                    break;
                case "2":
                    changeTarget();
                    break;
                case "3":
                    loadMessages("messages.txt");
                    break;
                case "4":
                    editMode();
                    break;
                case "0":
                	saveConfig("config.ini");
                    System.exit(0);
                    break;
                default:
                    break;
            }
        }
    }

    private static void startAttack() {
        clearScreen();
        printHeader();
        LOGGER.info("Mode: " + mode + " | Target: " + webhookUrl);
        LOGGER.info("Messages: " + amount + ", Delay: " + delayMs + "ms");
        
        Random random = new Random();
        if ("SPAM".equals(mode) || "SPAM + DELETE".equals(mode)) {
            for (int i = 0; i < amount; i++) {
                String message = messages.get(random.nextInt(messages.size()));
                try {
                	Map<String, String> params = new HashMap<String, String>() {{
                	    put("content", message);
                	    put("username", "epichook");
                	}};
                	REQUESTS.sendPost(webhookUrl, params);
                	LOGGER.info("Sent " + message);
                    Thread.sleep(delayMs);
                } catch (Exception ignored) {
                	LOGGER.error("Failed to send message: " + ignored.getMessage());
                }
            }
        }

        if ("DELETE".equals(mode) || "SPAM + DELETE".equals(mode)) {
            LOGGER.warn("Deleting webhook...");
            try {
                @SuppressWarnings("unused")
				String response = REQUESTS.sendDelete(webhookUrl);
                LOGGER.info("Webhook deleted successfully!");
            } catch (Exception e) {
                LOGGER.error("Failed to delete webhook: " + e.getMessage());
            }
        }

        LOGGER.info("Done. Press Enter to return to the menu...");
        SCANNER.nextLine();
    }

    private static void changeTarget() {
        System.out.print("Enter a new Webhook URL:\n> ");
        webhookUrl = SCANNER.nextLine();
        System.out.println("[✓] The URL has been updated. Press Enter...");
        SCANNER.nextLine();
    }

    private static void loadMessages(String filename) {
        messages.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(new File(filename)))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) messages.add(line.trim());
            }
        } catch (Exception e) {
//            LOGGER.error("Failed to load messages: " + e.getMessage());
        }
    }

    private static void editMode() {
        System.out.println("\nSelect a mode:");
        System.out.println(" [1] SPAM");
        System.out.println(" [2] SPAM + DELETE");
        System.out.println(" [3] DELETE");
        System.out.print(" > ");
        String choice = SCANNER.nextLine();
        switch (choice) {
            case "1": mode = "SPAM"; break;
            case "2": mode = "SPAM + DELETE"; break;
            case "3": mode = "DELETE"; break;
            default: break;
        }

        System.out.print("\nEnter the amount of messages (current: " + amount + "): ");
        try {
            int newAmount = Integer.parseInt(SCANNER.nextLine());
            if (newAmount > 0) amount = newAmount;
        } catch (Exception ignored) {}

        System.out.print("Enter the delay between messages in ms (current: " + delayMs + "): ");
        try {
            int newDelay = Integer.parseInt(SCANNER.nextLine());
            if (newDelay >= 0) delayMs = newDelay;
        } catch (Exception ignored) {}
    }

    private static void printHeader() {
        System.out.println(Ansi.RED + "            _      _                 _    \r\n"
        		+ "  ___ _ __ (_) ___| |__   ___   ___ | | __\r\n"
        		+ " / _ \\ '_ \\| |/ __| '_ \\ / _ \\ / _ \\| |/ /\r\n"
        		+ "|  __/ |_) | | (__| | | | (_) | (_) |   < \r\n"
        		+ " \\___| .__/|_|\\___|_| |_|\\___/ \\___/|_|\\_\\\r\n"
        		+ "     |_|                                  " + Ansi.SANE + "\n");
    }

    private static void clearScreen() {
        try {
            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
        	LOGGER.warn("Could not clear screen.");
        }
    }
    
    private static void loadConfig(String path) {
        Properties props = new Properties();
        try (FileReader reader = new FileReader(path)) {
            props.load(reader);
            webhookUrl = props.getProperty("webhook", webhookUrl);
            amount = Integer.parseInt(props.getProperty("amount", String.valueOf(amount)));
            delayMs = Integer.parseInt(props.getProperty("delay", String.valueOf(delayMs)));
            mode = props.getProperty("mode", mode);
        } catch (IOException e) {
            LOGGER.warn("Failed to load config.ini. Default values are used.");
        }
    }

    private static void saveConfig(String path) {
        Properties props = new Properties();
        props.setProperty("webhook", webhookUrl);
        props.setProperty("amount", String.valueOf(amount));
        props.setProperty("delay", String.valueOf(delayMs));
        props.setProperty("mode", mode);
        try (FileWriter writer = new FileWriter(path)) {
            props.store(writer, "epichook <3");
        } catch (IOException e) {
            LOGGER.error("Failed to save config.ini: " + e.getMessage());
        }
    }
} 
