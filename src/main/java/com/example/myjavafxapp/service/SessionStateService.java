package com.example.myjavafxapp.service;

import com.example.myjavafxapp.model.Councilor;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SessionStateService {

    private final ReadOnlyObjectWrapper<Councilor> currentlySpeakingCouncilorProperty = new ReadOnlyObjectWrapper<>(null);
    private final ReadOnlyListWrapper<Councilor> oratorsListProperty = new ReadOnlyListWrapper<>(FXCollections.observableArrayList());

    // Public getters for ReadOnlyProperties for binding in controllers
    public ReadOnlyObjectProperty<Councilor> currentlySpeakingCouncilorProperty() {
        return currentlySpeakingCouncilorProperty.getReadOnlyProperty();
    }

    public ReadOnlyListProperty<Councilor> oratorsListProperty() {
        return oratorsListProperty.getReadOnlyProperty();
    }

    // Methods for PresidentSessionController to update state
    public void setCurrentlySpeaking(Councilor councilor) {
        currentlySpeakingCouncilorProperty.set(councilor);
    }

    public void addOrator(Councilor councilor) {
        if (councilor != null && !oratorsListProperty.contains(councilor)) {
            oratorsListProperty.add(councilor);
        }
    }

    public void removeOrator(Councilor councilor) {
        oratorsListProperty.remove(councilor);
    }

    public void removeOrator(int index) {
        if (index >= 0 && index < oratorsListProperty.size()) {
            oratorsListProperty.remove(index);
        }
    }

    public void clearOrators() {
        oratorsListProperty.clear();
    }

    public Councilor getNextOrator() {
        if (!oratorsListProperty.isEmpty()) {
            return oratorsListProperty.get(0);
        }
        return null;
    }

    // This method might be more complex depending on exact logic,
    // e.g., if PresidentController directly sets currentlySpeaking and removes from list.
    // For now, PresidentController will handle the transition.
    // public void promoteNextOrator() {
    // if (!oratorsListProperty.isEmpty()) {
    // Councilor nextSpeaker = oratorsListProperty.get(0);
    // setCurrentlySpeaking(nextSpeaker);
    // // Orators are typically removed *after* they finish speaking.
    // }
    // }

    // Helper to get the current list if needed, though binding is preferred
    public List<Councilor> getOratorsList() {
        return oratorsListProperty.get();
    }
}
