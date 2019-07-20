package kr.ac.snu.hcil.enlaunchercontrolpanel.viewmodel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MappingContainer {
    public static List<String> visVarStringList  = new ArrayList<>(
            Arrays.asList("Motion", "Position", "Shape", "Size", "Color")
    );
    public static List<String> notiPropStringList = new ArrayList<>(
            Arrays.asList("None", "Importance", "Interaction Stage", "Keyword")
    );
    public static List<String> aggregateOPStringList = new ArrayList<>(
            Arrays.asList("None", "Min", "Max", "Average", "Most Frequent")
    );

    public static boolean isBothContinuous(String vis, String noti) {
        return (!isNominal(vis)) && (!isNominal(noti));
    }

    public static boolean isNominal(String var) {
        if (visVarStringList.contains(var)) {
            return !(var.equals("Position") || var.equals("Size"));
        } else {
            return !(var.equals("Importance"));
        }
    }

    String visVar, notiProp;

    public MappingContainer(String visVar, String notiProp) {
        this.visVar = visVar;
        this.notiProp = notiProp;


    }
}
