package com.animal.scale.hodoo.domain.single;

import com.animal.scale.hodoo.domain.Pet;
import com.animal.scale.hodoo.domain.PetBasicInfo;
import com.animal.scale.hodoo.domain.PetChronicDisease;
import com.animal.scale.hodoo.domain.PetPhysicalInfo;
import com.animal.scale.hodoo.domain.PetWeightInfo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class PetAllInfo {

    private static PetAllInfo instance;
    private PetAllInfo () {}

    public static PetAllInfo getInstance () {
        if ( instance == null )
            instance = new PetAllInfo();
        return instance;
    }

    public void print () {
        System.out.println("It's print() method in LazyInitialization instance.");
        System.out.println("instance hashCode > " + instance.hashCode());
    }

    @Getter
    @Setter
    public Pet pet;

    @Getter
    @Setter
    public PetBasicInfo petBasicInfo;

    @Getter
    @Setter
    public PetChronicDisease petChronicDisease;

    @Getter
    @Setter
    public PetPhysicalInfo petPhysicalInfo;

    @Getter
    @Setter
    public PetWeightInfo petWeightInfo;

    public float getFactor() {
        double factor = 0;
        //나이 <= 4개월
        if (petBasicInfo.getCurrentMonth() != 0 && petBasicInfo.getNeutralization() != null && petWeightInfo.getBcs() != 0) {
            if (petBasicInfo.getCurrentMonth() <= 4) {
                //2 (중성화)
                if (petBasicInfo.getNeutralization().matches("YES")) {
                    if (petWeightInfo.getBcs() <= 3) {
                        //2-1
                        factor = 1.6;
                    } else if (petWeightInfo.getBcs() == 4) {
                        //2-2
                        factor = 1.4;
                    } else {
                        //2-3
                        factor = 1.0;
                    }
                } else if (petBasicInfo.getNeutralization().matches("NO")) {
                    if (petWeightInfo.getBcs() <= 3) {
                        //1-1
                        factor = 3.0;
                    } else if (petWeightInfo.getBcs() == 4) {
                        //1-2
                        factor = 1.4;
                    } else {
                        //1-3
                        factor = 1.0;
                    }
                }
                //5 < 나이 <= 12개월
            } else if (petBasicInfo.getCurrentMonth() <= 12) {
                if (petBasicInfo.getNeutralization().matches("YES")) {
                    if (petWeightInfo.getBcs() <= 3) {
                        //3-1
                        factor = 2.0;
                    } else if (petWeightInfo.getBcs() == 4) {
                        //3-2
                        factor = 1.4;
                    } else {
                        //3-3
                        factor = 1.0;
                        factor = 1.0;
                    }
                } else if (petBasicInfo.getNeutralization().matches("NO")) {
                    if (petWeightInfo.getBcs() <= 3) {
                        //4-1
                        factor = 1.6;
                    } else if (petWeightInfo.getBcs() == 4) {
                        //4-2
                        factor = 1.4;
                    } else {
                        //4-3
                        factor = 1.0;
                    }
                }
            } else {
                //5
                if (petBasicInfo.getNeutralization().matches("YES")) {
                    if (petWeightInfo.getBcs() <= 3) {
                        //5-1
                        factor = 1.6;
                    } else if (petWeightInfo.getBcs() == 4) {
                        //5-2
                        factor = 1.4;
                    } else {
                        //5-3
                        factor = 1.0;
                    }
                } else if (petBasicInfo.getNeutralization().matches("NO")) {
                    //6
                    if (petWeightInfo.getBcs() <= 3) {
                        //6-1
                        factor = 1.8;
                    } else if (petWeightInfo.getBcs() == 4) {
                        //6-2
                        factor = 1.4;
                    } else {
                        //6-3
                        factor = 1.0;
                    }
                }
            }
        }else{
            factor = 0;
        }
        //Log.e("HJLEE", "factor : " + Float.parseFloat(String.valueOf(factor)));
        return Float.parseFloat(String.valueOf(factor));
    }

    @Override
    public String toString() {
        return "PetAllInfos [pet=" + pet + ", petBasicInfo=" + petBasicInfo + ", petChronicDisease=" + petChronicDisease
                + ", petPhysicalInfo=" + petPhysicalInfo + ", petWeightInfo=" + petWeightInfo + "]";
    }
}
