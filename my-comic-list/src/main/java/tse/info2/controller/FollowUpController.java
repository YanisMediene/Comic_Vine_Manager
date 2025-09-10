package tse.info2.controller;

import tse.info2.model.Issue;
import tse.info2.model.Volume;
import tse.info2.service.FollowUpService;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class FollowUpController {
    private FollowUpService followUpService;

    public FollowUpController() {
        this.followUpService = new FollowUpService();
    }

    public Map<Volume, List<Issue>> getSeriesFollowUp(int userId) {
        try {
            return followUpService.getSeriesFollowUp(userId);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
