package ling;

import markup.Sentence;
import markup.Token;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jld
 */
public class Lemma extends Features {

    int start;

    int end1;     //non-stop word lemmas in Q
    int end2;     //non-stop word lemmas in A
    int end3;     //non-stop word lemmas in Expl

    int end4;     //non-stop word common lemmas in Q+Expl
    int end5;     //non-stop word common lemmas in A+Expl
    int end6;     //non-stop word common lemmas in Q+A+Expl

    Map<String, Integer> lemmaIndexed = new HashMap<>();

    @Override
    public int getFirstSize() {
        return end1;
    }

    @Override
    public int getLastSize() {
        return end6;
    }

    public void setUniFeatures(Token token) {
        if (lemmaIndexed.isEmpty()) lemmaIndexed.put(token.getLemma(), 0);
        else if (!lemmaIndexed.containsKey(token.getLemma())) lemmaIndexed.put(token.getLemma(), lemmaIndexed.size());
        //System.out.println(lemmaIndexed);
    }

    public void setFeatureSizes(int s) {
        start = s;

        end1 = start + lemmaIndexed.size() + 1;   //lemmas in Q
        //System.out.println(end1);
        end2 = end1 + lemmaIndexed.size() + 1;  //lemmas in A
        //System.out.println(end2);
        end3 = end2 + lemmaIndexed.size() + 1;  //lemmas in Expl
        //System.out.println(end3);

        end4 = end3 + lemmaIndexed.size() + 1;   //lemmas in Q + Expl
        //System.out.println(end4);
        end5 = end4 + lemmaIndexed.size() + 1;   //lemmas in A + Expl
        //System.out.println(end5);
        end6 = end5 + lemmaIndexed.size() + 1;   //lemmas in Q + A + Expl
        //System.out.println(end6);
    }

    public String toSVMRankString(Sentence question, Sentence correctAns, Sentence expl) {

        /*System.out.println(Utils.getFeature(start, lemmaIndexed, question.getLemmas(), end1));
        System.out.println(Utils.getFeature(end1, lemmaIndexed, correctAns.getLemmas(), end2));
        System.out.println(Utils.getFeature(end2, lemmaIndexed, expl.getLemmas(), end3));
        System.out.println(Utils.getFeature(end3, lemmaIndexed, Utils.getCommon(List.copyOf(question.getLemmas()), List.copyOf(expl.getLemmas())), end4));
        System.out.println(Utils.getFeature(end4, lemmaIndexed, Utils.getCommon(List.copyOf(correctAns.getLemmas()), List.copyOf(expl.getLemmas())), end5));
        System.out.println(Utils.getFeature(end5, lemmaIndexed, Utils.getCommon(Utils.getGroup(List.copyOf(question.getLemmas()), List.copyOf(correctAns.getLemmas())), List.copyOf(expl.getLemmas())), end6));*/

        return Utils.getFeature(start, lemmaIndexed, question.getLemmas(), end1)+" "+
                Utils.getFeature(end1, lemmaIndexed, correctAns.getLemmas(), end2) +" "+
                Utils.getFeature(end2, lemmaIndexed, expl.getLemmas(), end3) +" "+
                Utils.getFeature(end3, lemmaIndexed, Utils.getCommon(List.copyOf(question.getLemmas()), List.copyOf(expl.getLemmas())), end4) + " " +
                Utils.getFeature(end4, lemmaIndexed, Utils.getCommon(List.copyOf(correctAns.getLemmas()), List.copyOf(expl.getLemmas())), end5) + " " +
                Utils.getFeature(end5, lemmaIndexed, Utils.getCommon(Utils.getGroup(List.copyOf(question.getLemmas()), List.copyOf(correctAns.getLemmas())), List.copyOf(expl.getLemmas())), end6);
    }

}
