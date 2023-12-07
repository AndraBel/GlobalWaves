package main;

import checker.Checker;
import checker.CheckerConstants;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.LibraryInput;
import main.Users.User;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Objects;

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
        ArrayList<Command> commands = objectMapper.readValue(
                new File("input/" + filePathInput),
                new TypeReference<ArrayList<Command>>() {
                });

        for (Command command : commands) {
            User user = users.get(command.getUsername());

            String commandType = command.getCommand();
            if (user == null && !command.getCommand().equals("getOnlineUsers")
                    && !command.getCommand().equals("addUser")
                    && !command.getCommand().equals("addAlbum")
                    && !command.getCommand().equals("showAlbums")
                    && !command.getCommand().equals("addEvent")
                    && !command.getCommand().equals("addMerch")
                    && !command.getCommand().equals("getAllUsers")
                    && !command.getCommand().equals("deleteUser")
                    && !command.getCommand().equals("addPodcast")
                    && !command.getCommand().equals("addAnnouncement")
                    && !command.getCommand().equals("removeAnnouncement")
                    && !command.getCommand().equals("showPodcasts")
                    && !command.getCommand().equals("removeAlbum")
                    && !command.getCommand().equals("removePodcast")
                    && !command.getCommand().equals("removeEvent")
                    && !command.getCommand().equals("getTop5Songs")
                    && !command.getCommand().equals("getTop5Playlists")
                    && !command.getCommand().equals("getTop5Albums")
                    && !command.getCommand().equals("getTop5Artists")) {
                ObjectNode resultNode = objectMapper.createObjectNode();
                resultNode.put("command", command.getCommand());
                resultNode.put("user", command.getUsername());
                resultNode.put("timestamp", command.getTimestamp());
                if ((mainLibrary.getArtists().containsKey(command.getUsername())
                        || mainLibrary.getHosts().containsKey(command.getUsername())
                        && command.getCommand().equals("switchConnectionStatus"))) {
                    resultNode.put("message", command.getUsername()
                            + " is not a normal user.");
                } else {
                    resultNode.put("message", "The username "
                            + command.getUsername() + " doesn't exist.");
                }


                outputs.add(resultNode);
                continue;
            }
            switch (commandType) {
                case "search":
                    outputs.add(user.search(command, mainLibrary));
                    break;
                case "select":
                    outputs.add(user.select(command, mainLibrary));
                    break;
                case "load":
                    outputs.add(user.load(command, mainLibrary));
                    break;
                case "playPause":
                    outputs.add(user.playPause(command));
                    break;
                case "repeat":
                    outputs.add(user.repeat(command));
                    break;
                case "shuffle":
                    outputs.add(user.shuffle(command));
                    break;
                case "forward":
                case "backward":
                    outputs.add(user.forwardBackward(command));
                    break;
                case "next":
                case "prev":
                    outputs.add(user.nextPrev(command));
                    break;
                case "like":
                    outputs.add(user.like(command));
                    break;
                case "addRemoveInPlaylist":
                    outputs.add(user.addRemoveInPlaylist(command));
                    break;
                case "status":
                    outputs.add(user.status(command));
                    break;
                case "createPlaylist":
                    outputs.add(user.createPlaylist(command));
                    break;
                case "switchVisibility":
                    outputs.add(user.switchVisibility(command));
                    break;
                case "follow":
                    outputs.add(user.followPlaylist(command));
                    break;
                case "showPlaylists":
                    outputs.add(user.showPlaylists(command));
                    break;
                case "showPreferredSongs":
                    outputs.add(user.showPreferredSongs(command));
                    break;
                case "getTop5Songs":
                    outputs.add(mainLibrary.getTop5Songs(command));
                    break;
                case "getTop5Playlists":
                    outputs.add(mainLibrary.getTop5Playlists(command));
                    break;
                case "switchConnectionStatus":
                    outputs.add(user.switchConnectionStatus(command));
                    break;
                case "getOnlineUsers":
                    outputs.add(mainLibrary.getOnlineUsers(command));
                    break;
                case "addUser":
                    outputs.add(mainLibrary.addUser(command));
                    break;
                case "addAlbum":
                    outputs.add(mainLibrary.addAlbum(command));
                    break;
                case "showAlbums":
                    outputs.add(mainLibrary.showAlbums(command));
                    break;
                case "showPodcasts":
                    outputs.add(mainLibrary.showPodcasts(command));
                    break;
                case "addEvent":
                    outputs.add(mainLibrary.addEvent(command));
                    break;
                case "addMerch":
                    outputs.add(mainLibrary.addMerch(command));
                    break;
                case "printCurrentPage":
                    outputs.add(user.printCurrentPage(command));
                    break;
                case "getAllUsers":
                    outputs.add(mainLibrary.getAllUsers(command));
                    break;
                case "addPodcast":
                    outputs.add(mainLibrary.addPodcast(command));
                    break;
                case "changePage":
                    outputs.add(user.changePage(command));
                    break;
                case "deleteUser":
                    outputs.add(mainLibrary.deleteUser(command));
                    break;
                case "addAnnouncement":
                    outputs.add(mainLibrary.addAnnouncement(command));
                    break;
                case "removeAnnouncement":
                    outputs.add(mainLibrary.removeAnnouncement(command));
                    break;
                case "removeAlbum":
                    outputs.add(mainLibrary.removeAlbum(command, mainLibrary));
                    break;
                case "removePodcast":
                    outputs.add(mainLibrary.removePodcast(command, mainLibrary));
                    break;
                case "removeEvent":
                    outputs.add(mainLibrary.removeEvent(command));
                    break;
                case "getTop5Albums":
                    outputs.add(mainLibrary.getTop5Albums(command));
                    break;
                case "getTop5Artists":
                    outputs.add(mainLibrary.getTop5Artists(command));
                    break;
                default:
                    // Handle unknown command
                    break;
            }
        }

        ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
        objectWriter.writeValue(new File(filePathOutput), outputs);
    }
}
