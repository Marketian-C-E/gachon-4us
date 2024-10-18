package co.kr.myfitnote.model;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class ManagerFragment {

    private List<Fragment> fragments = new ArrayList<>();
    private int index = 0;

    public List<Fragment> getFragments() {
        return fragments;
    }

    public void setFragments(List<Fragment> fragments) {
        this.fragments = fragments;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void add(Fragment fragment){
        fragments.add(fragment);
    }


    public Fragment getFragment(int index){
        return fragments.get(index);
    }

    public Fragment getCurrentFragment(){
//        Log.e()
        return fragments.get(index);
    }
//    public TestStartedFragment getCuurentFragment(){
//        return fragments.get(index);
//    }


    public int getSize(){
        return fragments.size();
    }

    public void changeFragment(int index){

    }

}
