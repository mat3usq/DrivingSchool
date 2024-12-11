document.addEventListener('DOMContentLoaded', () => {
    const threeOptions = document.querySelectorAll('.table-three-options');
    threeOptions.forEach(option => {
        option.addEventListener('click', () => {
            option.classList.toggle('clicked');
        });
    });

    const twoOptions = document.querySelectorAll('.table-two-options');
    twoOptions.forEach(option => {
        option.addEventListener('click', () => {
            option.classList.toggle('clicked');
        });
    });

    const oneOption = document.querySelectorAll('.table-one-option');
    oneOption.forEach(option => {
        option.addEventListener('click', () => {
            option.classList.toggle('clicked');
        });
    });

    function createPieChart(data, elementId, title) {
        const width = 400;
        const height = 400;
        const radius = height / 4;


          if (!Array.isArray(data) || data.length === 0) {
            return;
        }

        const allZero = data.every(d => d.value === 0);
        if (allZero) {
            return;
        }

        const color = d3.scaleOrdinal()
            .domain(data.map(d => d.label))
            .range(d3.schemeCategory10);

        const svg = d3.select(`#${elementId}`)
            .append('svg')
            .attr('width', width)
            .attr('height', height)
            .append('g')
            .attr('transform', `translate(${width / 2}, ${height / 2})`);

        const pie = d3.pie()
            .value(d => d.value)
            .sort(null);

        const data_ready = pie(data);

        const arc = d3.arc()
            .innerRadius(0)
            .outerRadius(radius);

        svg.selectAll('slices')
            .data(data_ready)
            .enter()
            .append('path')
            .attr('d', arc)
            .attr('fill', d => color(d.data.label))
            .attr("stroke", "white")
            .style("stroke-width", "2px");

        svg.append("text")
            .attr("text-anchor", "middle")
            .attr("y", -radius - 20)
            .style("font-size", "14px")
            .text(title);

        const legend = svg.append('g')
            .attr('transform', `translate(${radius + 20}, -radius)`);

        legend.selectAll('rect')
            .data(data)
            .enter()
            .append('rect')
            .attr('x', - (width / 4) )
            .attr('y', (d, i) => i * 20 + 120)
            .attr('width', 10)
            .attr('height', 10)
            .style('fill', d => color(d.label));

        legend.selectAll('legend-labels')
            .data(data)
            .enter()
            .append('text')
            .attr('x', - (width / 4) + 20 )
            .attr('y', (d, i) => i * 20 + 120 + 7)
            .text(d => `${d.label} (${d.value})`)
            .style('font-size', '12px')
            .attr('alignment-baseline', 'middle');
    }

    if (typeof examStatistics !== 'undefined' && examStatistics != null) {
        const examData = [
            { label: 'Rozwiązane Egzaminy', value: examStatistics.numberOfSolvedExams },
            { label: 'Zaliczone Egzaminy', value: examStatistics.numberOfPassedExams }
        ];
        createPieChart(examData, 'exam-chart', 'Egzaminy');
    }

    if (typeof examStatistics !== 'undefined' && examStatistics != null) {
        const answerData = [
            { label: 'Poprawne Odpowiedzi', value: examStatistics.numberOfCorrectAnswers },
            { label: 'Błędne Odpowiedzi', value: examStatistics.numberOfIncorrectAnswers },
            { label: 'Pominięte Pytania', value: examStatistics.numberOfSkippedQuestions }
        ];
        createPieChart(answerData, 'answer-chart', 'Odpowiedzi');
    }

    if (typeof testStatsData !== 'undefined' && testStatsData != null) {
        const testAnswerData = [
            { label: 'Poprawne Odpowiedzi', value: testStatsData.numberOfQuestionsAnsweredCorrectly },
            { label: 'Błędne Odpowiedzi', value: testStatsData.numberOfQuestionsAnsweredInCorrectly },
            { label: 'Pominięte Pytania', value: testStatsData.numberOfQuestionsSkipped }
        ];
        createPieChart(testAnswerData, 'test-answer-chart', 'Odpowiedzi w Testach');
    }
});

