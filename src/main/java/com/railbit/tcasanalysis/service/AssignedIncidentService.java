package com.railbit.tcasanalysis.service;


import com.railbit.tcasanalysis.entity.AssignedIncident;

import java.util.List;

public interface AssignedIncidentService {
    AssignedIncident getAssignedIncidentById(Long id);
    List<AssignedIncident> getAllAssignedIncident();
    AssignedIncident postAssignedIncident(AssignedIncident assignedIncident);
    void updateAssignedIncident(AssignedIncident assignedIncident);
    void deleteAssignedIncidentById(Long id);
}
