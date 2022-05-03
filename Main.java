package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

import ucb.util.CommandArgs;

import static enigma.EnigmaException.*;

/** Enigma simulator.
 *  @author Nithish Rajan
 */
public final class Main {

    /** Process a sequence of encryptions and decryptions, as
     *  specified by ARGS, where 1 <= ARGS.length <= 3.
     *  ARGS[0] is the name of a configuration file.
     *  ARGS[1] is optional; when present, it names an input file
     *  containing messages.  Otherwise, input comes from the standard
     *  input.  ARGS[2] is optional; when present, it names an output
     *  file for processed messages.  Otherwise, output goes to the
     *  standard output. Exits normally if there are no errors in the input;
     *  otherwise with code 1. */
    public static void main(String... args) {
        try {
            CommandArgs options =
                new CommandArgs("--verbose --=(.*){1,3}", args);
            if (!options.ok()) {
                throw error("Usage: java enigma.Main [--verbose] "
                            + "[INPUT [OUTPUT]]");
            }

            _verbose = options.contains("--verbose");
            new Main(options.get("--")).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /** Open the necessary files for non-option arguments ARGS (see comment
      *  on main). */
    Main(List<String> args) {
        _config = getInput(args.get(0));
        if (args.size() > 1) {
            _input = getInput(args.get(1));
        } else {
            _input = new Scanner(System.in);
        }

        if (args.size() > 2) {
            _output = getOutput(args.get(2));
        } else {
            _output = System.out;
        }
    }

    /** Return a Scanner reading from the file named NAME. */
    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Return a PrintStream writing to the file named NAME. */
    private PrintStream getOutput(String name) {
        try {
            return new PrintStream(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Configure an Enigma machine from the contents of configuration
     *  file _config and apply it to the messages in _input, sending the
     *  results to _output. */
    private void process() {
        Machine enigma = readConfig();
        if (!_input.hasNext("[*]")) {
            throw EnigmaException.error("Wrong Settings .");
        }
        String next = _input.nextLine();
        while (_input.hasNext()) {
            String setting = next;
            if (!setting.contains("*")) {
                throw new EnigmaException("Wrong Settings Format");
            }
            setUp(enigma, setting);
            next = _input.nextLine();
            while (next.length() == 0) {
                _output.println();
                next = _input.nextLine();
            }
            while (!next.contains("*")) {
                printMessageLine(enigma.convert(next.replaceAll(" ", "")));
                if (!_input.hasNext()) {
                    break;
                } else {
                    next = _input.nextLine();
                }
            }
        }
    }

    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */
    private Machine readConfig() {
        try {
            _availableRotors = new ArrayList<Rotor>();
            String alphabet = _config.next();
            if (!alphabet.matches("^((?![()* ])[\\x00-\\xFF])+")) {
                throw EnigmaException.error("Illegal Alphabet Given");
            }
            _alphabet = new Alphabet(alphabet);
            if (!_config.hasNextInt()) {
                throw EnigmaException.error("Integer not Given for numRotors");
            }
            int numRotor = _config.nextInt();
            if (!_config.hasNextInt()) {
                throw EnigmaException.error("Integer not Given for numPawls");
            }
            int numPawl = _config.nextInt();
            if (numPawl >= numRotor || numPawl == 0) {
                throw EnigmaException.error("Bad # of rotors and pawls given.");
            }
            while (_config.hasNext()) {
                _availableRotors.add(readRotor());
            }
            return new Machine(_alphabet, numRotor, numPawl, _availableRotors);
        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }

    /** Return a rotor, reading its description from _config. */
    private Rotor readRotor() {
        try {
            String name = _config.next().toUpperCase();
            String type = _config.next().toUpperCase();
            String cycles = "";
            while (_config.hasNext("\\s*[(].*[)]\\s*")) {
                String space = _config.next().replaceAll("[)][(]", ") (");
                cycles += space + " ";
            }
            if (type.length() > 1) {
                String pawl = type.substring(1);
                Permutation perm = new Permutation(cycles, _alphabet);
                if (type.charAt(0) == 'M') {
                    return new MovingRotor(name, perm, pawl);
                }
            }
            if (type.charAt(0) == 'N') {
                return new FixedRotor(name, new Permutation(cycles, _alphabet));
            }
            if (type.charAt(0) == 'R') {
                return new Reflector(name, new Permutation(cycles, _alphabet));
            } else {
                throw EnigmaException.error("Incorrect Rotor Type Given");
            }
        } catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }
    }

    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment. */
    private void setUp(Machine M, String settings) {
        if (settings == "") {
            throw EnigmaException.error("No Config Given");
        }
        listMaker();
        Scanner rotorSettings = new Scanner(settings);
        String[] rotors = new String[M.numRotors()];
        String plugboardSetup = "";
        if (!rotorSettings.hasNext("[*]")) {
            throw EnigmaException.error("Wrong Settings Format");
        }
        rotorSettings.next();
        int x = 0;
        while (x != M.numRotors()) {
            rotors[x] = rotorSettings.next().toUpperCase();
            x++;
        }
        for (int i = 0; i < rotors.length - 1; i++) {
            for (int j = i + 1; j < rotors.length; j++) {
                if (rotors[i].equals(rotors[j])) {
                    throw  EnigmaException.error("Duplicate Rotor");
                }
            }
        }
        if (!rotorSettings.hasNext()) {
            throw EnigmaException.error("General Error");
        }
        if (rotors.length > M.numRotors()) {
            throw EnigmaException.error("Wrong number of Rotors passed in.");
        }
        if (!_ref.contains("Rotor " + rotors[0])) {
            throw EnigmaException.error("1st rotor not reflector.");
        }
        for (int t = 0; t < rotors.length; t++) {
            boolean movingBool = !_mov.contains("Rotor " + rotors[t]);
            boolean reflectBool = !_ref.contains("Rotor " + rotors[t]);
            boolean fixedBool = !_fix.contains("Rotor " + rotors[t]);
            if (!fixedBool && !reflectBool && !movingBool) {
                throw EnigmaException.error("Bad Rotor Name!");
            }
        }
        String setter = rotorSettings.next();
        M.insertRotors(rotors);
        M.setRotors(setter);
        if (rotorSettings.hasNext("(\\w{4})")) {
            String ring = rotorSettings.next();
            M.ringstellung(ring);
        }
        if (rotorSettings.hasNext("\\(\\w{2}\\)")) {
            while (rotorSettings.hasNext("\\(\\w{2}\\)")) {
                plugboardSetup += rotorSettings.next() + " ";
            }
        } else {
            plugboardSetup = "";
        }
        M.setPlugboard(new Permutation(plugboardSetup, _alphabet));
        rotorConfig(M);
    }

    /** Return true iff verbose option specified. */
    static boolean verbose() {
        return _verbose;
    }

    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private void printMessageLine(String msg) {
        if (msg == "") {
            _output.println();
        }
        while (msg != "") {
            if (msg.length() <= 5) {
                _output.println(msg);
                msg = "";
            } else {
                _output.print(msg.substring(0, 5) + " ");
                msg = msg.substring(5);
            }
        }
    }

    /** Makes String Lists of the Rotors Available. */
    void listMaker() {
        _mov = new ArrayList<String>();
        _fix = new ArrayList<String>();
        _ref = new ArrayList<String>();
        for (int x = 0; x < _availableRotors.size(); x++) {
            String added = _availableRotors.get(x).toString();
            if (_availableRotors.get(x).rotates()) {
                _mov.add(added);
            } else if (_availableRotors.get(x).reflecting()) {
                _ref.add(added);
            } else {
                _fix.add(added);
            }
        }
    }
    /** Checks the Right Configuration of Rotors using machine M. */
    void rotorConfig(Machine M) {
        int diff = M.numRotors() - M.numPawls();
        for (int x = 1; x < diff - 1; x++) {
            if (M.rotors().get(x).rotates()) {
                throw EnigmaException.error("Wrong Rotor Config.");
            }
        }
        for (int y = diff; y < M.numRotors(); y++) {
            if (!M.rotors().get(y).rotates()) {
                throw EnigmaException.error("Wrong Rotor Config.");
            }
        }
    }

    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private Scanner _input;

    /** Source of machine configuration. */
    private Scanner _config;

    /** File for encoded/decoded messages. */
    private PrintStream _output;

    /** True if --verbose specified. */
    private static boolean _verbose;

    /** The Arraylist of available Rotors. */
    private ArrayList<Rotor> _availableRotors;

    /** String List of the Moving Rotors Available. */
    private ArrayList<String> _mov;

    /** String List of the Fixed Rotors Available. */
    private ArrayList<String> _fix;

    /** String List of the Reflectors Available. */
    private ArrayList<String> _ref;


}
