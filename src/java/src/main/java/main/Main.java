package main;

import ling.Lemma;
import ling.TableStore;
import utils.IO;
import utils.NLP;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author jld
 */
public class Main {

    //Features
    Lemma lemma;
    TableStore ts;

    public Main() {
        lemma = new Lemma();
        ts = new TableStore();
    }

    public Lemma getLemma() {
        return lemma;
    }

    public TableStore getTs() {
        return ts;
    }

    public void initDevSetup(String data_dir, String feat_grp) throws IOException {
        Dev dev = new Dev(this);
        String qa_file = "Elem-Dev-Ling.csv";
        dev.processQA(IO.readCSV(data_dir+"\\"+qa_file, '\t', 0));
        String expl_file = "expl-tablestore-ling.csv";
        dev.processExpl(IO.readCSV(data_dir+"\\"+expl_file, '\t', 0));
        String gold_data = "Elem-Dev-Expl.csv";
        dev.processPositiveQAExpl(IO.readCSV(data_dir+"\\"+gold_data, '\t', 0));
        String output_file = "dev-"+feat_grp+".dat";
        String id_file = "dev-ids-"+feat_grp+".txt";
        dev.writeOutput(new FileOutputStream(data_dir+"\\"+output_file), new FileOutputStream(data_dir+"\\"+id_file));
    }

    public void initTrainSetup(String data_dir, int numNeg, String feat_grp) throws IOException {
        Train train = new Train(this);
        String qa_file = "Elem-Train-Ling.csv";
        train.processQA(IO.readCSV(data_dir+"\\"+qa_file, '\t', 0));
        String expl_file = "expl-tablestore-ling.csv";
        train.processExpl(IO.readCSV(data_dir+"\\"+expl_file, '\t', 0));
        lemma.setFeatureSizes(0);
        ts.setFeatureSizes(lemma.getLastSize());

        System.out.println("Done generating training features!");

        String gold_data = "Elem-Train-Expl.csv";
        train.processPositiveQAExpl(IO.readCSV(data_dir+"\\"+gold_data, '\t', 0));
        String output_file = "train-"+numNeg+"-"+feat_grp+".dat";
        String id_file = "train-ids-"+numNeg+"-"+feat_grp+".txt";
        //train.writeOutput(new FileOutputStream(data_dir+"\\"+output_file));

        /*train.generateNegativeQAExpl(numNeg);
        train.writePosAndNegSelectInstances(new FileOutputStream(data_dir+"\\"+output_file),
                new FileOutputStream(data_dir+"\\"+id_file));*/

        System.out.println("Done writing training data!");
        /*Select s = new Select();
        String heu_data = "Elem-main.Train-Expl-heu1.csv";
        String heu_output_file = "train_heu1.dat";
        s.init(IO.readCSV(data_dir+"\\"+heu_data, '\t', 0));
        train.writeSelectedInstances(new FileOutputStream(data_dir+"\\"+heu_output_file), s.getAnnotationsMap());*/

    }

    public static void main(String[] args) throws IOException {
        String data_dir = "C:\\Users\\DSouzaJ\\Desktop\\Code\\TG-2019\\data";

        NLP.setStopwords(IO.readFile(data_dir+"\\resources\\stopwords", StandardCharsets.UTF_8).split("\\n"));
        NLP.setConcepts(IO.readCSV(data_dir+"\\resources\\concepts.txt", '\t', 0));

        Main main = new Main();
        String feat_grp = "lemma";
        main.initTrainSetup(data_dir, 500, feat_grp);
        main.initDevSetup(data_dir, feat_grp);
    }

}
