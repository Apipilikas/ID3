# ID3 Algorithm
### implemented in Java
-------------------
EVANGELOS PIPILIKAS

## File compilation
    1. Open the command line
    2. Change the directory to the one that contains the project
    3. Run the command: javac ID3.java

## Running the algorithm
   ~~~~
   >!< Please pay attention to the order that parameters should be given. >!<
   ~~~~
   * To execute the algorithm, make sure you have put the train and data files
      at the same folder with algorithm ( or make sure you put the right directory).
   * The algorithm will expect the above parameters with the exact same order that are described:
     * featureFileName: the file name that contains the vocabulary (.vocab file)
     * trainDataFileName: the file name of the training examples (.feat file)
     * testDataFileName: the file name of the test examples (.feat file)
     * pruningParameter: the parameter we use to prune the tree (double and should be between 0.0 and 1.0, we recommend > 0.80 and <0.97)
     * nParameter: the first n most common words
   * Here is an example of input: `java ID3 imdb.vocab trainlabeledBow.feat testlabeledBow.feat 0.90 100`
~~~~
>!< In case there is an OutOfMemoryError, please use less words in the .vocab file. >!<
~~~~

Source of data in test_examples folder: https://ai.stanford.edu/~amaas/data/sentiment/
