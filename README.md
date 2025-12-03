This is a very bad CLI task manager, that's it :/
If you somehow want to set up this piece of "code" (honestly, I'm shocked) git clone it and then:
  1. Go to the task-manager-CLI directory and run: ``` mvn clean package -v ```
  2. Then run the program with: ``` java -jar target/task-manager-1.0.jar <COMMAND>``` Your first command will probably be --help.
  3. Optionally, you can create a bash script
