package team13813.mode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import team13813.state.Facing;
import team13813.state.State;
import team13813.state.Team;
import team13813.util.Configuration;

@TeleOp(name = "KokiTeleNext", group = "TeleOp")
public class KokiTeleNext extends KokiAutoNext {

    @Override
    public void setTeam() {
        Configuration.setTeam(Team.BLUE);
    } // TODO: subsitude this with a new value

    @Override
    public void setFacing() {
        Configuration.setFacing(Facing.WALLBREAKER);
    } // TODO: subsitude this with a new value

    @Override
    public void setState() {
        Configuration.setState(State.CONTROL);
    }
}
