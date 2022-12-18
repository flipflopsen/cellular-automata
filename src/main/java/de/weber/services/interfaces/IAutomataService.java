package de.weber.services.interfaces;

import de.weber.model.Automaton;

public interface IAutomataService {
    Automaton createDefaultFolderAndFile();
    void createNewSimulationFromFile(String filename);
    void saveContentsToFile(String contents);
    String getFileContents(String automatonName);
    void removeAutomatonSession(String automatonName);
    void clearAutomataFolder();
    boolean validateFileExistence(String filename);
    Automaton retrieveSimulationFromFile(String filename);
    String compileAndSetAutomaton(String contents, String simulationIdentifier);
}
