package team13813.mode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import team13813.Configuration;
import team13813.state.Facing;
import team13813.state.State;
import team13813.state.Team;

@TeleOp(name = "WallfacerKokiTeleNext", group = "OpMode")
public class WallfacerKokiTeleNext extends KokiAutoNext {
    @Override
    public void setTeam() {
        Configuration.setTeam(Team.BLUE);
    }

    @Override
    public void setFacing() {
        Configuration.setFacing(Facing.WALLBREAKER);
    }

    @Override
    public void setState() {
        Configuration.setState(State.AUTONOMOUS);
    }
}
