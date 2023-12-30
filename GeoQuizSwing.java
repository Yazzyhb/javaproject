import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GeoQuizSwing extends JFrame {

    private JLabel quizLabel;
    private JLabel questionLabel;

    private JButton[] answerButtons;
    private JButton nextButton;
    private JButton pauseButton;
    private JButton resetButton;
    private JLabel timerLabel;

    private int currentQuestionIndex;
    private int score;

    private String[][] questions = {
            { "What is the capital of Australia?", "Sydney", "Canberra", "Melbourne", "Brisbane", "Canberra" },
            { "In which continent is the Amazon Rainforest located?", "Africa", "South America", "Asia",
                    "North America", "South America" },
            // Add more questions as needed
    };

    private Timer timer;
    private int secondsLeft;

    public GeoQuizSwing() {
        setTitle("GEO Quiz");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(255, 182, 193)); // Pastel pink background

        initializeComponents();
        startQuiz();
    }

    private void initializeComponents() {
        JPanel quizPanel = new JPanel();
        quizPanel.setLayout(new BoxLayout(quizPanel, BoxLayout.Y_AXIS));

        quizLabel = new JLabel("[QUIZ]");
        quizLabel.setFont(new Font("Arial", Font.BOLD, 30));
        quizLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        quizPanel.add(quizLabel);

        questionLabel = new JLabel();
        questionLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        quizPanel.add(questionLabel);

        answerButtons = new JButton[4];
        JPanel answerPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        for (int i = 0; i < 4; i++) {
            answerButtons[i] = new JButton();
            answerButtons[i].setFont(new Font("Arial", Font.PLAIN, 18));
            answerButtons[i].addActionListener(new AnswerButtonListener());
            answerPanel.add(answerButtons[i]);
        }
        quizPanel.add(answerPanel);

        timerLabel = new JLabel();
        timerLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        quizPanel.add(timerLabel);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10)); // Added space between buttons
        nextButton = new JButton("Next");
        nextButton.addActionListener(new NextButtonListener());
        buttonPanel.add(nextButton);

        pauseButton = new JButton("Pause");
        pauseButton.addActionListener(new PauseButtonListener());
        buttonPanel.add(pauseButton);

        resetButton = new JButton("Reset");
        resetButton.addActionListener(new ResetButtonListener());
        buttonPanel.add(resetButton);

        quizPanel.add(buttonPanel);

        add(quizPanel, BorderLayout.CENTER);
    }

    private void startQuiz() {
        currentQuestionIndex = 0;
        score = 0;
        secondsLeft = 10;
        initializeTimer();
        showQuestion();
    }

    private void initializeTimer() {
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                secondsLeft--;
                updateTimerLabel();
                if (secondsLeft < 0) {
                    timer.stop();
                    handleNextQuestion();
                }
            }
        });
    }

    private void updateTimerLabel() {
        timerLabel.setText("Time remaining: " + secondsLeft + " seconds");
    }

    private void showQuestion() {
        timer.start();
        updateTimerLabel();

        String[] currentQuestion = questions[currentQuestionIndex];
        quizLabel.setText("[QUIZ]");
        int questionNo = currentQuestionIndex + 1;
        int totalQuestions = questions.length; // Total number of questions
        questionLabel.setText("<html><div style='text-align: center;'>" + "Question " + questionNo + " of "
                + totalQuestions + ": " + currentQuestion[0] + "</div></html>");
        for (int i = 0; i < 4; i++) {
            answerButtons[i]
                    .setText("<html><div style='text-align: center;'>" + currentQuestion[i + 1] + "</div></html>");
            answerButtons[i].setEnabled(true);
            answerButtons[i].setBackground(null); // Reset background color
            answerButtons[i].setOpaque(true); // Make sure the button is opaque
            answerButtons[i].setBorderPainted(true); // Make sure the button border is painted
            answerButtons[i].addActionListener(new AnswerButtonListener());
        }

        nextButton.setEnabled(false);
    }

    void showScore() {
        quizLabel.setText("[QUIZ]");
        timerLabel.setText("");

        String message;
        if (score > 0) {
            message = "Congratulations! You've scored " + score + " out of " + questions.length + ".";
        } else {
            message = "Good luck next time. You didn't score any points.";
        }

        // Display message with custom "Try Again" button
        Object[] options = { "Play Again" };
        int result = JOptionPane.showOptionDialog(this, message, "Quiz Finished",
                JOptionPane.YES_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

        if (result == JOptionPane.YES_OPTION) {
            SwingUtilities.invokeLater(() -> {
                getContentPane().removeAll(); // Remove all components from the content pane
                initializeComponents(); // Reinitialize components
                revalidate();
                repaint();
                startQuiz(); // Start the quiz again
            });
        }
    }

    private void resetQuiz() {
        remove(quizLabel);
        remove(questionLabel);
        for (int i = 0; i < 4; i++) {
            remove(answerButtons[i]);
        }

        remove(timerLabel);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(nextButton);
        buttonPanel.add(pauseButton);
        buttonPanel.add(resetButton);

        add(quizLabel, BorderLayout.NORTH);
        add(questionLabel, BorderLayout.CENTER);
        add(answerButtons[0], BorderLayout.WEST);
        add(answerButtons[1], BorderLayout.EAST);
        add(answerButtons[2], BorderLayout.SOUTH);
        add(answerButtons[3], BorderLayout.SOUTH);
        add(timerLabel, BorderLayout.SOUTH);
        add(buttonPanel, BorderLayout.SOUTH);

        revalidate();
        repaint();

        startQuiz();
    }

    private void handleNextQuestion() {
        timer.stop();
        if (currentQuestionIndex < questions.length - 1) {
            currentQuestionIndex++;
            secondsLeft = 10;
            showQuestion();
        } else {
            showScore();
            currentQuestionIndex = 0;
            score = 0;
            nextButton.setText("Next");
        }
    }

    private class AnswerButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            timer.stop();
            JButton selectedButton = (JButton) e.getSource();
            String selectedAnswer = getButtonTextWithoutHtml(selectedButton);

            for (int i = 0; i < 4; i++) {
                answerButtons[i].setEnabled(false);
            }

            String correctAnswer = getButtonTextWithoutHtml(answerButtons[0]); // Assuming correct answer is in the
                                                                               // first button

            System.out.println("Selected Answer: " + selectedAnswer);
            System.out.println("Correct Answer: " + correctAnswer);

            if (selectedAnswer.equals(correctAnswer)) {
                selectedButton.setBackground(Color.GREEN);
                score++;
            } else {
                selectedButton.setBackground(Color.RED);

                for (JButton button : answerButtons) {
                    if (getButtonTextWithoutHtml(button).equals(correctAnswer)) {
                        button.setBackground(Color.GREEN);
                        break;
                    }
                }
            }

            nextButton.setEnabled(true);
        }

        private String getButtonTextWithoutHtml(JButton button) {
            return button.getText().replaceAll("\\<.*?\\>", "");
        }
    }

    private class NextButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            handleNextQuestion();
        }
    }

    private class PauseButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            timer.stop();
            JOptionPane.showMessageDialog(GeoQuizSwing.this, "Quiz paused. Click OK to resume.", "Paused",
                    JOptionPane.INFORMATION_MESSAGE);
            timer.start();
        }
    }

    private class ResetButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            getContentPane().removeAll(); // Remove all components from the content pane

            initializeComponents(); // Reinitialize components

            revalidate();
            repaint();

            startQuiz(); // Start the quiz again
            
        }
    }
    

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new GeoQuizSwing().setVisible(true);
        });
      
    }
}