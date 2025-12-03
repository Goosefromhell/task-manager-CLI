package org.govnorgatization.taskmanagercli;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Main {
    public final Set<String> COMMANDS = Set.of("add", "delete-all", "update", "list", "delete", "--help", "mark", "mark <tags>", "tags:", "done", "in-progress", "todo");

    static void write(ObjectMapper mapper, File target, List<Map<String, Object>> list) {
        mapper.writerWithDefaultPrettyPrinter().writeValue(target, list);


    }

    void main(String[] args) {
        LocalDateTime dateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        if (!COMMANDS.contains(args[0])) {
            System.err.println("Usage: java ... Main <name> <description>");
            return;
        }

        Path target = Path.of(System.getProperty("user.home"), "Buffers", "TaskManager", "test.json");

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

                    HashMap<String, Object> new_task = new HashMap<>();
                    new_task.put("description", String.join(" ", Arrays.copyOfRange(args, 1, args.length)));
                    int last_od = list.isEmpty() ? 0 : Integer.parseInt(list.getLast().get("id").toString());
                    new_task.put("id", String.valueOf(last_od + 1));
                    new_task.put("marked", "todo");
                    new_task.put("created", dateTime.format(formatter));
                    new_task.put("updated", "");
                    list.add(new_task);

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
                            list.get(i).replace("description", String.join(" ", Arrays.copyOfRange(args, 2, args.length)));
                            list.get(i).replace("updated", dateTime.format(formatter));
                            write(mapper, target.toFile(), list);
                            break;
                        }
                    }
                    break;


                case "mark":
                    if (args.length < 3 || !COMMANDS.contains(args[2])) {
                        System.err.println("Usage: java ... Main <name> <description>");
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

                    } else if (args.length > 1 && !COMMANDS.contains(args[1])) {
                        System.err.println("Usage: java ... Main <name> <description>");
                        break;
                    }
                    System.out.printf("%-4s %-35s %-12s %-20s %-20s%n", "ID", "DESCRIPTION", "STATUS", "CREATED", "UPDATED");
                    for (Map<String, Object> task : list) {
                        String title = task.get("description").toString();
                        int length_of_title = title.length();
                        String if_longer = title;
                        title = title.length() > 30 ? title.substring(0, 30) : title;
                        if (has_filter) {
                            if (task.get("marked").equals(args[1])) {
                                System.out.printf("%-4s %-35s %-12s %-20s %-20s%n", task.get("id"), title, task.get("marked"), task.get("created"), task.get("updated"));
                                if (length_of_title > 30) {
                                    System.out.printf("     %s%n", if_longer.substring(30));
                                }
                            }
                        } else {
                            System.out.printf("%-4s %-35s %-12s %-20s %-20s%n", task.get("id"), title, task.get("marked"), task.get("created"), task.get("updated"));
                            if (length_of_title > 30) {
                                System.out.printf("     %s%n", if_longer.substring(30));
                            }
                        }

                    }

                    break;
                case "delete": {
                    boolean filtered = false;
                    if (args.length != 2) {
                        System.err.println("Usage: java ... Main <name> <description>");
                    } else {
                        if (COMMANDS.contains(args[1])) {
                            filtered = true;
                        }
                        for (int i = list.size() - 1; i >= 0; i--) {

                            if (filtered && list.get(i).get("marked").toString().equals(args[1])) {
                                list.remove(i);
                                write(mapper, target.toFile(), list);
                            } else if (!filtered && list.get(i).get("id").toString().equals(args[1])) {
                                list.remove(i);
                                write(mapper, target.toFile(), list);
                                break;
                            }
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


        } catch (IOException e) {
            System.err.println("Unable to write task file: " + e.getMessage());
        }
    }


}
