package hcil.snu.ac.kr.enlaunchercontrolpanel.ViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MappingContainer {
    public static List<String> visVarStringList  = new ArrayList<>(
            Arrays.asList("motion", "position", "shape", "size", "color")
    );
    public static List<String> notiPropStringList = new ArrayList<>(
            Arrays.asList("None", "Importance", "Interaction Stage", "Keyword")
    );

    String visVar, notiProp;
    boolean isVisVarNominal, isNotiPropNominal;


    public MappingContainer(String visVar, String notiProp) {
        this.visVar = visVar;
        this.notiProp = notiProp;


    }
}
