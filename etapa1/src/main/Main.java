package main;

import checker.Checker;
import checker.CheckerConstants;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import fileio.input.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * The entry point to this homework. It runs the checker that tests your implentation.
 */
public final class Main {
    static final String LIBRARY_PATH = CheckerConstants.TESTS_PATH + "library/library.json";

    /**
     * for coding style
     */
    private Main() {
    }

    /**
     * DO NOT MODIFY MAIN METHOD
     * Call the checker
     *
     * @param args from command line
     * @throws IOException in case of exceptions to reading / writing
     */
    public static void main(final String[] args) throws IOException {
        File directory = new File(CheckerConstants.TESTS_PATH);
        Path path = Paths.get(CheckerConstants.RESULT_PATH);

        if (Files.exists(path)) {
            File resultFile = new File(String.valueOf(path));
            for (File file : Objects.requireNonNull(resultFile.listFiles())) {
                file.delete();
            }
            resultFile.delete();
        }
        Files.createDirectories(path);

        for (File file : Objects.requireNonNull(directory.listFiles())) {
            if (file.getName().startsWith("library")) {
                continue;
            }

            String filepath = CheckerConstants.OUT_PATH + file.getName();
            File out = new File(filepath);
            boolean isCreated = out.createNewFile();
            if (isCreated) {
                action(file.getName(), filepath);
            }
        }

        Checker.calculateScore();
    }

    /**
     * @param filePathInput  for input file
     * @param filePathOutput for output file
     * @throws IOException in case of exceptions to reading / writing
     */
    public static void action(final String filePathInput,
                              final String filePathOutput) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        LibraryInput library = objectMapper.readValue(new File(LIBRARY_PATH), LibraryInput.class);

        ArrayNode outputs = objectMapper.createArrayNode();

        //Creating library object and populate with data
        Library mainLibrary = new Library(library);

        LinkedHashMap<String, User> users = mainLibrary.getUsers();
        ArrayList<Command> commands = objectMapper.readValue(new File("input/" + filePathInput), new TypeReference<ArrayList<Command>>() {
        });

        for (Command command : commands) {
            User user = users.get(command.getUsername());
            if (command.getCommand().equals("search")) {
                outputs.add(user.search(command));
            }
            if (command.getCommand().equals("select")) {
                outputs.add(user.select(command));
            }
            if (command.getCommand().equals("load")) {
                outputs.add(user.load(command, mainLibrary));
            }
            if (command.getCommand().equals("playPause")) {
                outputs.add(user.playPause(command));
            }
            if (command.getCommand().equals("repeat")) {
                outputs.add(user.repeat(command));
            }
            if (command.getCommand().equals("shuffle")) {
                outputs.add(user.shuffle(command));
            }
            if (command.getCommand().equals("forward") || command.getCommand().equals("backward")) {
                outputs.add(user.forwardBackword(command));
            }
            if (command.getCommand().equals("next") || command.getCommand().equals("prev")) {
                outputs.add(user.nextPrev(command));
            }
            if (command.getCommand().equals("like")) {
                outputs.add(user.like(command));
            }
            if (command.getCommand().equals("addRemoveInPlaylist")) {
                outputs.add(user.addRemoveInPlaylist(command));
            }
            if (command.getCommand().equals("status")) {
                outputs.add(user.status(command));
            }
            if (command.getCommand().equals("createPlaylist")) {
                outputs.add(user.createPlaylist(command));
            }
            if (command.getCommand().equals("switchVisibility")) {
                outputs.add(user.switchVisibility(command));
            }
            if (command.getCommand().equals("follow")) {
                outputs.add(user.follow(command));
            }
            if (command.getCommand().equals("showPlaylists")) {
                outputs.add(user.showPlaylists(command));
            }
            if (command.getCommand().equals("showPreferredSongs")) {
                outputs.add(user.showPreferredSongs(command));
            }
            if (command.getCommand().equals("getTop5Songs")) {
                outputs.add(mainLibrary.getTop5Songs(command));
            }
            if (command.getCommand().equals("getTop5Playlists")) {
                outputs.add(mainLibrary.getTop5Playlists(command));
            }
        }
        ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
        objectWriter.writeValue(new File(filePathOutput), outputs);
    }
}
