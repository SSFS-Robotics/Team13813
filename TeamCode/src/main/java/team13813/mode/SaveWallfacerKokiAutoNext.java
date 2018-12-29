package team13813.mode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import team13813.Configuration;
import team13813.state.Facing;
import team13813.state.State;
import team13813.state.Team;

@Autonomous(name = "SaveWallfacerKokiAutoNext", group = "Save")
public class SaveWallfacerKokiAutoNext extends KokiAutoNext {

    @Override
    public void setTeam() {
        Configuration.setTeam(Team.BLUE);
    }

    @Override
    public void setFacing() {
        Configuration.setFacing(Facing.WALLFACER);
    }

    @Override
    public void setState() {
        Configuration.setState(State.RECORDING);
    }
}
