package mod.kerzox.automaton.client.loader.animation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface IAnimatable {

    IAnimatable getParent();
    List<IAnimatable> getChildren();
    Map<String, Joint> getAllJoints();

}
