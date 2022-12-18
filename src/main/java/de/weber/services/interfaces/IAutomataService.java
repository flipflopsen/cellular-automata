package de.weber.services.interfaces;

public interface IAutomataService {
    void createDefaultFolderAndFile(String filename);
    void createNewSimulationFromFile(String filename);
    void saveContentsToFile(String contents);
    String getFileContents(String automatonName);
}
