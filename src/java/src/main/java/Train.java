import ling.SvmRankFeatures1;
import markup.Data;
import markup.Explanation;
import markup.QA;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jld
 */
public class Train {

    Map<String, QA> idQa;
    Map<String, Explanation> idExpl;
    Map<String, List<String>> posAnn;

    public Train() {
        idQa = new HashMap<>();
        idExpl = new HashMap<>();
        posAnn = new HashMap<>();
    }

    public void processExpl(List<String[]> lines) {
        for (String[] line : lines) {
            Explanation obj = new Explanation(line[0], line[2]);
            obj.setTokens(line[1].split("\\s"));
            idExpl.put(line[0], obj);
            SvmRankFeatures1.populateLists(obj.getSentence());
            SvmRankFeatures1.setTablestore(obj.getSource());
        }
    }

    public void processQA(List<String[]> lines) {
         for (String[] line : lines) {
             QA obj = new QA(line);
             idQa.put(line[0], obj);
             SvmRankFeatures1.populateLists(obj.getQuestion());
             SvmRankFeatures1.populateLists(obj.getOptionA());
             SvmRankFeatures1.populateLists(obj.getOptionB());
             if (line.length >= 6) SvmRankFeatures1.populateLists(obj.getOptionC());
             if (line.length >= 7) SvmRankFeatures1.populateLists(obj.getOptionD());
        }
    }

    public void processPositiveQAExpl(List<String[]> lines) {
        for (String[] line : lines) {
            String quesID = line[0];
            String explID = line[1];
            List<String> annotations = posAnn.get(line[0]);
            if (annotations == null) posAnn.put(line[0], annotations = new ArrayList<>());
            annotations.add(line[1]);
        }
    }

    public void writeSelectedInstances(FileOutputStream output, Map<String, Map<String, Integer>> annotations) throws IOException {
        int qid = 1;
        for (String qaID : annotations.keySet()) {

            QA qa = idQa.get(qaID);

            Map<String, Integer> subannotations = annotations.get(qaID);

            for (String explID : subannotations.keySet()) {
                Explanation expl = idExpl.get(explID);
                int rank = subannotations.get(explID);

                String featureStr = SvmRankFeatures1.toString(qa.getQuestion(), qa.getCorrectAns(), expl.getSentence(), expl.getSource()).replaceAll("\\s+", " ");
                String outputStr = rank + " qid:" + qid + " " + featureStr;
                output.write((outputStr + "\n").getBytes());
            }

            qid++;
        }
    }

    public void writeOutput(FileOutputStream output) throws IOException {
        int qid = 1;
        for (String qaID : posAnn.keySet()) {
            QA qa = idQa.get(qaID);

            List<String> explanationIDs = posAnn.get(qaID);
            int rank = explanationIDs.size()+1;
            for (String explanationID : explanationIDs) {
                Explanation expl = idExpl.get(explanationID);

                String featureStr = SvmRankFeatures1.toString(qa.getQuestion(), qa.getCorrectAns(), expl.getSentence(), expl.getSource()).replaceAll("\\s+", " ");
                String outputStr = rank+" qid:"+qid+" "+featureStr;
                output.write((outputStr+"\n").getBytes());

                rank--;
            }

            if (rank != 1) {
                System.out.println("~strange");
                System.exit(-1);
            }
            for (String explID : idExpl.keySet()) {
                if (explanationIDs.contains(explID)) continue;

                Explanation expl = idExpl.get(explID);

                String featureStr = SvmRankFeatures1.toString(qa.getQuestion(), qa.getCorrectAns(), expl.getSentence(), expl.getSource()).replaceAll("\\s+", " ");
                String outputStr = rank+" qid:"+qid+" "+featureStr;
                output.write((outputStr+"\n").getBytes());
            }

            qid++;
        }
    }

}

