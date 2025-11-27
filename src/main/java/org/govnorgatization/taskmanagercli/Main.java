package org.govnorgatization.taskmanagercli;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    static void main(String[] args) {
        if (args.length < 2 &&!args[0].contains("--")) {
            System.err.println("Usage: java ... Main <name> <description>");
            return;
        }

        TaskLoader taskLoader = new TaskLoader();

        Path target = Path.of(System.getProperty("user.home"),
                "Buffers", "TaskManager", "test.json");

//            Map<String, Object> map = mapper.convertValue(json, new TypeReference<>() {});
//            map.put("koka","soka");
//            taskLoader.header.putAll(map);
//            System.out.println(map);
//            for (JsonNode node : json) {
//                System.out.println(node);
//            }
        try {
            Files.createDirectories(target.getParent());

            ObjectMapper mapper = new ObjectMapper();
            List<Map<String, Object>> list;
            list = mapper.readValue(target.toFile(), new TypeReference<>() {
            });
            String[] commands = {"add","update","list","delete","--done","--inprogress","--todo"};
            switch (args[0]) {
                case "--help":
    System.out.println(commands.toString());
                case "add":
                    HashMap<String, Object> new_taks = new HashMap<>();
                    new_taks.put("header", args[1]);
                    new_taks.put("discription", args[2]);
                    new_taks.put("id", String.valueOf(list.size()));
                    list.add(new_taks);

                    mapper.writerWithDefaultPrettyPrinter()
                            .writeValue(target.toFile(), list);
                    break;
                default:
                    System.err.println("Unknown command: " + args[0] + "write --help to get list of commands");

            }


            System.out.println(list.getLast());


//            System.out.println( json.get("command"));
        } catch (IOException e) {
            System.err.println("Unable to write task file: " + e.getMessage());
        }
    }

    public static class TaskLoader {
        public String[] keks;
    }
}
