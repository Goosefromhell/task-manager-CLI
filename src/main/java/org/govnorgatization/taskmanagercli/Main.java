package org.govnorgatization.taskmanagercli;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Main {
    public final Set<String> COMMANDS = Set.of("add", "delete-all", "update", "list", "delete", "--help", "mark", "mark <tags>", "tags:", "done", "in-progress", "todo");

    static void write(ObjectMapper mapper, File target, List<Map<String, Object>> list) {
        mapper.writerWithDefaultPrettyPrinter()
                .writeValue(target, list);


    }

    void main(String[] args) {
        if (!COMMANDS.contains(args[0])) {
            System.err.println("Usage: java ... Main <name> <description>");
            return;
        }

        Path target = Path.of(System.getProperty("user.home"),
                "Buffers", "TaskManager", "test.json");

        try {
            Files.createDirectories(target.getParent());

            ObjectMapper mapper = new ObjectMapper();
            List<Map<String, Object>> list;
            list = mapper.readValue(target.toFile(), new TypeReference<>() {
            });

            switch (args[0]) {
                case "--help":
                    for (String command : COMMANDS) {
                        System.out.println(command);
                    }
                    break;
                case "add":

                    HashMap<String, Object> new_taks = new HashMap<>();
                    new_taks.put("discription", String.join(" ", Arrays.copyOfRange(args, 1, args.length)));
                    int last_od = list.isEmpty() ? 0 : Integer.parseInt(list.getLast().get("id").toString());
                    new_taks.put("id", String.valueOf(last_od + 1));
                    new_taks.put("marked", "todo");
                    list.add(new_taks);

                    write(mapper, target.toFile(), list);
                    break;
                case "update":
                    try {
                        Integer.parseInt(args[1]);
                    } catch (NumberFormatException e) {
                        System.err.println("Usage: java ... Main <name> <description>");
                        break;

                    }
                    if (args.length < 3 || !Arrays.toString(args).contains(args[2])) {
                        System.err.println("Usage: java ... Main <name> <description>");
                        break;
                    }
                    if (Integer.parseInt(list.getLast().get("id").toString()) < Integer.parseInt(args[1])) {
                        System.err.println("Usage: java ... Main <name> <description>");
                        break;
                    }

                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).get("id").toString().equals(args[1])) {
                            System.out.println(i);
                            list.get(i).replace("discription", String.join(" ", Arrays.copyOfRange(args, 2, args.length)));
                            write(mapper, target.toFile(), list);
                            break;
                        }
                    }
                    break;


                case "mark":
                    if (args.length < 3 || !COMMANDS.contains(args[2])) {
                        System.err.println("Ussssage: java ... Main <name> <description>");
                        break;
                    }
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).get("id").toString().equals(args[1])) {
                            list.get(i).replace("marked", args[2]);
                            write(mapper, target.toFile(), list);
                            break;
                        }
                    }
                    break;
                case "list":
                    if (args.length > 2) {
                        System.err.println("Usage: java ... Main <name> <description>");
                    }
                    boolean has_filter = false;
                    if (args.length > 1 && COMMANDS.contains(args[1])) {

                        has_filter = true;

                    } else if (args.length > 1 && !COMMANDS.contains(args[2])) {
                        System.err.println("Usage: java ... Main <name> <description>");
                        break;
                    }
                    for (Map<String, Object> task : list) {
                        String result;
                        if (has_filter) {
                            try {
                                result = task.get("marked").equals(args[1]) ? task.get("discription") + " " + task.get("marked").toString() : "\b";

                            } catch (Exception e) {
                                result = task.get("discription").toString();
                            }
                        } else {
                            try {
                                result = task.get("discription") + " " + task.get("marked").toString();

                            } catch (Exception e) {
                                result = task.get("discription").toString();
                            }

                        }
                        System.out.println(result);

                    }

                    break;
                case "delete": {
                    if (args.length != 2) {
                        System.err.println("Usage: java ... Main <name> <description>");
                    }

                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).get("id").toString().equals(args[1])) {
                            list.remove(i);
                            write(mapper, target.toFile(), list);
                            break;
                        }
                    }
                    break;
                }
                case "delete-all": {
                    list.clear();
                    write(mapper, target.toFile(), list);
                    break;
                }

                default:
                    System.err.println("Unknown command: " + args[0] + "write --help to get list of commands");
                    break;

            }


            System.out.println(list.getLast());


        } catch (IOException e) {
            System.err.println("Unable to write task file: " + e.getMessage());
        }
    }


}
