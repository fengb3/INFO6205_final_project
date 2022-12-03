import com.UI.WindowWordle;
import com.Wordle.Const;
import com.Wordle.Hacker.Benchmark;
import com.Wordle.Hacker.Entropy;
import com.Wordle.Hacker.Pattern;
import com.Wordle.Word;

import java.io.File;

public class Main
{
    public static void main(String[] args)
    {
//        new Thread(() -> CheckFiles()).start();

        CheckFiles();

        // Benchmark.Run();

        new WindowWordle();

//        Word.getWordFrequency(0);

    }

    private static void CheckFiles()
    {
        // Check if the files exist
        File patternMatrixFile = new File(Const.PATH_PATTEN_MATRIX);

        if(!patternMatrixFile.exists())
        {
            Pattern.loadPatternMatrix(true);
        }
        else
        {
            Pattern.loadPatternMatrix(false);
        }

        File totalInfoMapFile = new File(Const.PATH_INFO_MAP);

        if(!totalInfoMapFile.exists())
        {
            Entropy.loadInfoMap(true);
        }
        else
        {
            Entropy.loadInfoMap(false);
        }
    }
}