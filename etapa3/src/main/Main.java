package main;

import app.admin.Command;
import app.admin.Library;
import checker.Checker;
import checker.CheckerConstants;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.LibraryInput;
import app.users.user.User;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

/**
 * The entry point to this homework. It runs the checker that tests your implentation.
 */
public final class Main {
    static final String LIBRARY_PATH = CheckerConstants.TESTS_PATH + "library/library.json";
    private static final List<String> ALLOWED_COMMANDS = Arrays.asList(
            "getOnlineUsers", "addUser", "addAlbum", "showAlbums", "addEvent",
            "addMerch", "getAllUsers", "deleteUser", "addPodcast", "addAnnouncement",
            "removeAnnouncement", "showPodcasts", "removeAlbum", "removePodcast",
            "removeEvent", "getTop5Songs", "getTop5Playlists", "getTop5Albums",
            "getTop5Artists", "wrapped", "subscribe", "getNotifications", "buyMerch",
            "previousPage", "nextPage", "buyPremium", "cancelPremium"
    );

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

    private static void handleCommand(final Library mainLibrary,
                                      final ArrayNode outputs, final User user,
                                      final String commandType,
                                      final Command command) {
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
                outputs.add(user.changePage(command, mainLibrary));
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
            case "wrapped":
                outputs.add(mainLibrary.wrapped(command));
                break;
            case "subscribe":
                outputs.add(mainLibrary.subscribe(command));
                break;
            case "getNotifications":
                outputs.add(mainLibrary.getNotifications(command));
                break;
            case "buyMerch":
                outputs.add(mainLibrary.buyMerch(command));
                break;
            case "seeMerch":
                outputs.add(mainLibrary.seeMerch(command));
                break;
            case "updateRecommendations":
                outputs.add(mainLibrary.handleRecommendation(command));
                break;
            case "previousPage":
                outputs.add(user.previousPage(command));
                break;
            case "nextPage":
                outputs.add(user.nextPage(command));
                break;
            case "loadRecommendations":
                outputs.add(user.loadRecommendations(command, mainLibrary));
                break;
            case "buyPremium":
                outputs.add(mainLibrary.buyPremium(command));
                break;
            case "cancelPremium":
                outputs.add(mainLibrary.cancelPremium(command));
                break;
            case "adBreak":
                outputs.add(mainLibrary.adBreak(command));
                break;
            default:
                // Handle unknown command
                break;
        }
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

        // Access the singleton instance with lazy initialization
        Library mainLibrary = Library.getInstance(library);

        LinkedHashMap<String, User> users = mainLibrary.getUsers();
        ArrayList<Command> commands = objectMapper.readValue(
                new File("input/" + filePathInput),
                new TypeReference<ArrayList<Command>>() {
                });

        for (Command command : commands) {
            User user = users.get(command.getUsername());
            String commandType = command.getCommand();

            if (user == null && !ALLOWED_COMMANDS.contains(command.getCommand())) {
                ObjectNode resultNode = objectMapper.createObjectNode();
                resultNode.put("command", command.getCommand());
                resultNode.put("user", command.getUsername());
                resultNode.put("timestamp", command.getTimestamp());

                if ((mainLibrary.getArtists().containsKey(command.getUsername())
                        || mainLibrary.getHosts().containsKey(command.getUsername()))
                        && command.getCommand().equals("switchConnectionStatus")) {
                    resultNode.put("message", command.getUsername()
                            + " is not a normal user.");
                } else {
                    resultNode.put("message", "The username " + command.getUsername()
                            + " doesn't exist.");
                }

                outputs.add(resultNode);
                continue;
            }

            handleCommand(mainLibrary, outputs, user, commandType, command);
        }

        for (User user : mainLibrary.getUsers().values()) {
            user.getPlayer().calculateStatus(commands.getLast().getTimestamp());
        }

        outputs.add(mainLibrary.endProgram());

        mainLibrary.resetInstance();
        ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
        objectWriter.writeValue(new File(filePathOutput), outputs);
    }
}
