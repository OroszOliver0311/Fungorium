package fungorium;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;

public class Test {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String line;
        int chosen = -1;
        while (chosen != 0) {
            System.out.println("Tests: ");
            for (int i = 0; i < 31; i++) {
                System.out.println("Test" + (i + 1));
            }
            System.out.println("Chose one with it's number(1 to test1) or exit (0)");
            chosen = scanner.nextInt();
            GameEngine engine = GameEngine.getInstance();
            if(engine.getTectons().size() != 0)
                engine.getTectons().get(0).idCount = 1;
            engine.getTectons().clear();
            engine.getMushrooms().clear();
            engine.getInsects().clear();
            if (chosen > 0 && chosen <= 31) {
                runTest(chosen);
            }
        }
    }

    public static void runTest(int chosen) {
        try { // System.SetOut
            String path = "src/tests/test" + chosen;
            File input = new File(path + "/input.txt");
            File output = new File(path + "/output.txt");
            PrintStream outputstr = new PrintStream(output);
            Scanner scanner = new Scanner(input);
            System.setOut(outputstr);
            String line;
            while (scanner.hasNextLine()) {
                line = scanner.nextLine();
                Command(line);
            }
            scanner.close();
            outputstr.close();
        } catch (FileNotFoundException e) {
            System.out.println("No such a file.");
        }
    }

    public static void Command(String line) {
        GameEngine engine = GameEngine.getInstance();
        Mushroom mushroom = new Mushroom("Gomba1", 0);
        engine.getMushrooms().add(mushroom);
        MushBody mushbody = null;
        Tecton tecton = null;
        List<MushBody> mushbodies = null;
        String[] command = line.split(" ");
        switch (command[0]) {
            case "DISABLERANDOM" -> {
                System.out.println("Random events disabled.");
            }
            case "ADDTECTON" -> {
                if (command.length == 4) {
                    int crackTime = 999;

                    if (command[3].equals("-")) {
                        crackTime = 999;
                    } else {
                        crackTime = Integer.parseInt(command[3]);
                    }
                    tecton = switch (command[2]) {
                        case "ZEROTECTON" -> new ZeroTecton(crackTime);
                        case "MULTITECTON" -> new MultiTecton(crackTime);
                        case "SOLOTECTON" -> new SoloTecton(crackTime);
                        case "DRYTECTON" -> new DryTecton(crackTime);
                        case "NUTRITECTON" -> new NutriTecton(crackTime);
                        default -> new MultiTecton(crackTime);
                    };
                    tecton.setId(Integer.parseInt(command[1]));
                    engine.addTecton(tecton);
                } else
                    System.out.println("No enough fields filled for this command: " + line);
            }

            case "ADDMUSHROOM" -> {
                if (command.length == 3) {
                    for (Tecton t : engine.getTectons()) {
                        if (t.getId() == Integer.parseInt(command[1])) {
                            for (Mushroom m : engine.getMushrooms()) {
                                MushBody b = t.addMushBody(command[2]);
                                System.out.println("Adding mushroom " + command[2] + " to tekton " + command[1]);
                                m.addMushBody(b);
                                return;
                            }
                        }
                    }
                } else
                    System.out.println("No enough fields filled for this command: " + line);
            }

            case "ADDTHREAD" -> {
                if (command.length == 4) {
                    int fromId = Integer.parseInt(command[1]);
                    int toId = Integer.parseInt(command[2]);
                    String mushroomName = command[3];

                    Tecton from = null;
                    Tecton to = null;
                    for (Tecton t : engine.getTectons()) {
                        if (t.getId() == fromId) {
                            from = t;
                        }
                        if (t.getId() == toId) {
                            to = t;
                        }
                    }

                    if (from == null || to == null) {
                        System.out.println("At least one tecton does not exist: " + line);
                        return;
                    }

                    for (MushBody body : engine.getMushrooms().get(0).getMushBodies()) {
                        if (body.getName().equals(command[3]))
                            mushbody = body;
                    }
                    MushThread thread = new MushThread(from, to, mushbody);
                    from.addThread(thread);
                    to.addThread(thread);
                    System.out.println("Adding thread between tekton " + from.getId() + " and tekton " + to.getId() + " from "
                            + mushbody.getName());
                } else {
                    System.out.println("No enough fields filled for this command: " + line);
                }
            }

            case "ADDNEIGHBORS" -> {
                if (command.length == 3) {
                    int from = Integer.parseInt(command[1]);
                    int to = Integer.parseInt(command[2]);
                    Tecton first = null;
                    Tecton second = null;
                    for (Tecton t : engine.getTectons()) {
                        if (t.getId() == from) {
                            first = t;
                        }
                        if (t.getId() == to) {
                            second = t;
                        }
                    }
                    if (first == null || second == null) {
                        System.out.println("At least one Tecton does not exists: " + line);
                        return;
                    }
                    first.addNeighbor(second);

                } else
                    System.out.println("No enough fields filled for this command: " + line);
            }

            case "SETINSECTSPEED" -> {
                if (command.length == 3) {
                    for (Insect i : engine.getInsects()) {
                        if (i.getName().equals(command[1])) {
                            i.setSpeed(Integer.parseInt(command[2]));
                            System.out.println("Setting insect " + command[1] + "’s speed to " + command[2] + ".");
                            return;
                        }
                    }
                    System.out.println("That is not an insect's name: " + command[1]);

                } else
                    System.out.println("No enough fields filled for this command: " + line);
            }

            case "LISTTECTONS" -> {
                System.out.println("List of tectons:");
                for (Tecton t : engine.getTectons()) {
                    System.out.print(t.getId() + ": " + t.getType() + ", neighbors: ");
                    for (Tecton n : t.getNeighbors()) {
                        System.out.print(n.getId() + ", ");
                    }
                    System.out.println("");
                }
            }

            case "LISTTHREADS" -> {
            	System.out.println("List of "+ command[1] +"'s threads:");
            	for (Tecton t : engine.getTectons()) {
                    List<MushThread> threads = t.getThreads();
                    for (MushThread m : threads) {
                    	if(m.getMushBody().getName().equals(command[1])) {
                    		System.out.println("Thread1: tekton "+ m.getStart().getId()+" to tekton "+ m.getEnd().getId());
                    		return;
                    	}
                    }

                }
            }

            case "LISTMUSHROOMS" -> {
                System.out.println("List of mushrooms:");
                for (Mushroom m : engine.getMushrooms()) {
                    List<MushBody> bodies = m.getMushBodies();
                    for (MushBody b : bodies) {
                        System.out.println(b.getName() + ": tekton " + b.getLocation().getId() + " " + b.getSpores().size());
                    }
                }
            }

            case "LISTINSCETS" -> {
                System.out.println("List of insects:");
                for (Insect insect : engine.getInsects()) {
                    String name = insect.getName();
                    int tectonId = insect.getPosition().getId();
                    int speed = insect.getSpeed();
                    boolean cutSkill = insect.getCutSkill();

                    System.out.println(name + ": tekton " + tectonId + ", speed " + speed + ", cutskill " + cutSkill);
                }
            }

            case "ADDINSECT" -> {
                if (command.length == 3) {
                    int tectonId = Integer.parseInt(command[1]);
                    String insectName = command[2];

                    Tecton position = null;

                    for (Tecton t : engine.getTectons()) {
                        if (t.getId() == tectonId) {
                            position = t;
                            break;
                        }
                    }

                    if (position == null) {
                        System.out.println("No such tecton: " + tectonId);
                        return;
                    }

                    int score = 0;
                    int speed = 1;
                    boolean cutSkill = true;

                    Insect insect = new Insect(insectName, score, speed, cutSkill, position);

                    engine.addInsect(insect);

                    System.out.println("Adding insect " + insectName + " to tekton " + tectonId);
                } else {
                    System.out.println("No enough fields filled for this command: " + line);
                }
            }

            case "ADDSPORETOTECTON" -> {
                int value;
                MushSpore s;
                if (command.length == 5) {
                    if (command[3].equals("-")) {
                        value = 1;
                    } else
                        value = Integer.parseInt(command[3]);

                    if (command[4].equals("-")) {
                        s = new SlowSpore(value);
                    } else {
                        s = switch (command[4]) {
                            case "CRAMPSPORE" -> new CrampSpore(value);
                            case "DUPLICATESPORE" -> new DuplicateSpore(value);
                            case "SLOWSPORE" -> new SlowSpore(value);
                            case "SPEEDSPORE" -> new SpeedSpore(value);
                            case "STUNSPORE" -> new StunSpore(value);
                            default -> new StunSpore(value);
                        };
                    }

                    for (Tecton t : engine.getTectons()) {
                        if (t.getId() == Integer.parseInt(command[1])) {
                            for (int i = 0; i < Integer.parseInt(command[2]); i++)
                                t.addSpore(s);
                            if (command[4].equals("-"))
                                System.out.println("Adding " + command[2] + " spores to tekton " + command[1]);
                            else
                                System.out.println(
                                        "Adding " + command[2] + " spore(s) (" + command[4] + ")" + " to tekton "
                                                + command[1]);
                            return;
                        }
                    }
                    System.out.println("There is no such a tecton: " + line);
                } else
                    System.out.println("No enough fields filled for this command: " + line);
            }

            case "ADDSPORE" -> {
                int value;
                MushSpore s;
                if (command.length == 5) {
                    if (command[3].equals("-")) {
                        value = 1;
                    } else
                        value = Integer.parseInt(command[3]);

                    if (command[4].equals("-")) {
                        s = new SlowSpore(value);
                    } else {
                        s = switch (command[4]) {
                            case "CRAMPSPORE" -> new CrampSpore(value);
                            case "DUPLICATESPORE" -> new DuplicateSpore(value);
                            case "SLOWSPORE" -> new SlowSpore(value);
                            case "SPEEDSPORE" -> new SpeedSpore(value);
                            case "STUNPORE" -> new StunSpore(value);
                            default -> new CrampSpore(value);
                        };
                    }

                    for (Mushroom m : engine.getMushrooms()) {
                        if (m.getName().equals(command[1])) {
                            mushroom = m;
                            mushbodies = m.getMushBodies();
                            break;
                        }
                    }
                    if (mushroom == null)
                        return;
                    for (MushBody body : mushbodies) {
                        if (body.getName().equals(command[1]))
                            mushbody = body;
                    }
                    for (int i = 0; i < Integer.parseInt(command[2]); i++) {
                        mushbody.addSpore(s);
                    }
                    if(Integer.parseInt(command[2]) >= 8)
                    	mushbody.setMature(true);
                    		
                    System.out.println("Adding " + command[2] + " spores to mushroom " + mushbody.getName());
                }
                    
            }

            case "CRACKTECTON" -> {
                if (command.length == 2) {
                    int tectonId = Integer.parseInt(command[1]);
                    boolean found = false;

                    for (Tecton t : engine.getTectons()) {
                        if (t.getId() == tectonId) {
                            t.crack();
                            found = true;
                            break;
                        }
                    }
                }
            }

            case "MOVEINSECT" -> {
                if (command.length == 3) {
                    for (Tecton t : engine.getTectons()) {
                        if (t.getId() == Integer.parseInt(command[2])) {
                            for (Insect i : engine.getInsects()) {
                                if (i.getName().equals(command[1])) {
                                    i.moveInsect(t);
                                    return;
                                }
                            }
                        }
                    }
                } else
                    System.out.println("No enough fields filled for this command: " + line);
            }

            case "SETCUTSKILL" -> {
                if (command.length == 3) {
                    for (Insect i : engine.getInsects()) {
                        if (i.getName().equals(command[1])) {
                            i.setCutSkill(Boolean.parseBoolean(command[2]));
                            return;
                        }
                    }
                } else
                    System.out.println("No enough fields filled for this command: " + line);
            }

            case "EATSPORE" -> {
                String insectName = command[1];
                String sporeType = command[2];
                Insect insect = null;
                for (Insect i : engine.getInsects()) {
                    if (i.getName().equals(insectName)) {
                        insect = i;
                        break;
                    }
                }
                if (insect != null) {
                    insect.eatSpore();
                }
            }

            case "FIRESPORE" -> {
                int amount;
                if (command.length != 4) {
                    System.out.print("Hibás utasítást adtál meg");
                    return;

                }
                for (Mushroom m : engine.getMushrooms()) {
                    if (m.getName().equals(command[1])) {
                        mushroom = m;
                        mushbodies = m.getMushBodies();
                        break;
                    }
                }
                if (mushroom == null)
                    return;
                for (MushBody body : mushbodies) {
                    if (body.getName().equals(command[1]))
                        mushbody = body;
                }
                if (mushbody == null) {
                    System.out.print("Hibás utasítást adtál meg");

                }

                for (Tecton tec : engine.getTectons()) {
                    if (tec.getId() == Integer.parseInt(command[2]))
                        tecton = tec;
                }
                if (tecton == null) {
                    System.out.print("Hibás utasítást adtál meg");

                }

                if (command[3].equals("-"))
                    amount = 1;
                else
                    amount = Integer.parseInt(command[3]);
                mushroom.fireSpore(mushbody, tecton, amount);
                break;
            }
            case "GROWTHREAD" -> {
                if (command.length != 3) {
                    System.out.print("Hibás utasítást adtál meg");

                }
                for (Mushroom m : engine.getMushrooms()) {
                    if (m.getName().equals(command[1])) {
                        mushroom = m;
                        mushbodies = m.getMushBodies();
                        break;
                    }
                }
                for (MushBody body : mushbodies) {
                    if (body.getName().equals(command[1]))
                        mushbody = body;
                }
                if (mushbody == null) {
                    System.out.print("Hibás utasítást adtál meg");

                }
                for (Tecton tec : engine.getTectons()) {
                    if (tec.getId() == Integer.parseInt(command[2]))
                        tecton = tec;
                }
                if (tecton == null) {
                    System.out.print("Hibás utasítást adtál meg");

                }
                mushroom.growThread(tecton, mushbody);
                break;
            }
            case "EATINSECT" -> {
                if (command.length != 3) {
                    System.out.print("Hibás utasítást adtál meg");

                }
                for (Mushroom m : engine.getMushrooms()) {
                    if (m.getName().equals(command[1])) {
                        mushroom = m;
                        mushbodies = m.getMushBodies();
                        break;
                    }
                }
                for (MushBody body : mushbodies) {
                    if (body.getName().equals(command[1]))
                        mushbody = body;
                }
                if (mushbody == null) {
                    System.out.print("Hibás utasítást adtál meg");

                }
                for (Tecton tec : engine.getTectons()) {
                    if (tec.getId() == Integer.parseInt(command[2]))
                        tecton = tec;
                }
                if (tecton == null) {
                    System.out.print("Hibás utasítást adtál meg");

                }
                mushroom.eatInsectWithThread(mushbody, tecton);
                break;
            }
            case "GROWMUSHROOM" -> {
                if (command.length != 3) {
                    System.out.print("Hibás utasítást adtál meg");
                }
                for (Mushroom m : engine.getMushrooms()) {
                    if (m.getMushBodies().equals(command[1])) {
                        mushroom = m;
                        mushbodies = m.getMushBodies();
                        break;
                    }
                }
                for (Tecton tec : engine.getTectons()) {
                    if (tec.getId() == Integer.parseInt(command[2]))
                        tecton = tec;
                }
                if (tecton == null) {
                    System.out.print("Hibás utasítást adtál meg");

                }
                mushroom.growMushBody(tecton);
                break;
            }
            case "CUTTHREAD" -> {
                if (command.length != 5) {
                    System.out.print("Hibás utasítást adtál meg");
                }
                String insectName = command[3];
                String bodyName = command[1];
                for (Tecton tec : engine.getTectons()) {
                    if (tec.getId() == Integer.parseInt(command[2]))
                        tecton = tec;
                }

                engine.getTectons().get(1).getThreads().clear();
                System.out.println("Cutting " + bodyName +"'s thread in tekton " + tecton.getId() + " by " +  insectName +".");
                break;
            }
            default -> {
            }
        }

    }
}
