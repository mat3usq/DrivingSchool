document.addEventListener('DOMContentLoaded', () => {
    const options = document.querySelectorAll('.payment');
    options.forEach(option => {
        option.addEventListener('click', () => {
            option.classList.toggle('clicked');
        });
    });

    // Sprawdzenie, czy dane statystyczne są dostępne
    if (window.appData.examStatistics) {
        const examStatistics = window.appData.examStatistics;
        const testStatistics = window.appData.testStatistics;

        const examData = [
            {label: 'Rozwiązane Egzaminy', value: examStatistics.numberOfSolvedExams},
            {label: 'Zdane Egzaminy', value: examStatistics.numberOfPassedExams}
        ];

        const answerData = [
            {label: 'Poprawne Odpowiedzi', value: examStatistics.numberOfCorrectAnswers},
            {label: 'Błędne Odpowiedzi', value: examStatistics.numberOfIncorrectAnswers},
            {label: 'Pominięte Pytania', value: examStatistics.numberOfSkippedQuestions}
        ];

        function createPieChart(data, elementId, title) {
            const width = 300;
            const height = 300;
            const margin = 40;
            const radius = Math.min(width, height) / 2 - margin;

            const svg = d3.select(`#${elementId}`)
                .append('svg')
                .attr('width', width + 100)
                .attr('height', height + 100)
                .append('g')
                .attr('transform', `translate(${(width + 100) / 2},${(height + 50) / 2})`);

            const color = d3.scaleOrdinal()
                .domain(data.map(d => d.label))
                .range(d3.schemeCategory10);

            const pie = d3.pie()
                .value(d => d.value);

            const data_ready = pie(data);

            svg.selectAll('whatever')
                .data(data_ready)
                .join('path')
                .attr('d', d3.arc()
                    .innerRadius(0)
                    .outerRadius(radius))
                .attr('fill', d => color(d.data.label))
                .attr('stroke', '#fff')
                .style('stroke-width', '2px')
                .style('opacity', 0.7);

            svg.append('text')
                .attr('text-anchor', 'middle')
                .attr('y', -radius - 20)
                .style('font-size', '16px')
                .text(title);

            const legend = svg.append('g')
                .attr('transform', `translate(${-width/2}, ${radius + 30})`);

            legend.selectAll('legend-dots')
                .data(data)
                .enter()
                .append('rect')
                .attr('x', 0)
                .attr('y', (d, i) => i * 20)
                .attr('width', 10)
                .attr('height', 10)
                .style('fill', d => color(d.label));

            legend.selectAll('legend-labels')
                .data(data)
                .enter()
                .append('text')
                .attr('x', 20)
                .attr('y', (d, i) => i * 20 + 9)
                .text(d => `${d.label} (${d.value})`)
                .style('font-size', '12px')
                .attr('alignment-baseline', 'middle');
        }

        createPieChart(examData, 'exam-chart', 'Egzaminy');
        createPieChart(answerData, 'answer-chart', 'Odpowiedzi');

        // Tworzenie wykresów testów, jeśli dane są dostępne
        if (testStatistics) {
            const testAnswerData = [
                {label: 'Poprawne Odpowiedzi', value: testStatistics[1]},
                {label: 'Błędne Odpowiedzi', value: testStatistics[2]},
                {label: 'Pominięte Pytania', value: testStatistics[3]}
            ];

            createPieChart(testAnswerData, 'test-answer-chart', 'Odpowiedzi w Testach');
        }
    }
});

