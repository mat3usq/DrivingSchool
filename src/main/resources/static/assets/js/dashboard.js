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

    function createPieChart(data, elementId, title, const_color) {
        const width = 400;
        const height = 400;
        const radius = height / 3;
        const labelColor = '#f0f8ff';

        if (!Array.isArray(data) || data.length === 0) {
            return;
        }

        const allZero = data.every(d => d.value === 0);
        if (allZero) {
            return;
        }

        let color;

        if(const_color)
            color = d3.scaleOrdinal()
                .domain(data.map(d => d.label))
                .range(['#1dc996','#3a86ff', '#ff006e']);
        else
            color= d3.scaleOrdinal()
                .domain(data.map(d => d.label))
                .range(['#1dc996','#ff006e']);


        const svg = d3.select(`#${elementId}`)
            .append('svg')
            .attr('width', width)
            .attr('height', height + 100)
            .append('g')
            .attr('transform', `translate(${width / 2}, ${height / 2})`);

        const pie = d3.pie()
            .value(d => d.value)
            .sort(null);

        const data_ready = pie(data);

        const arc = d3.arc()
            .innerRadius(0)
            .outerRadius(radius);

        const total = d3.sum(data, d => d.value);

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
            .attr("y", -radius - 30)
            .style("font-size", "18px")
            .style("font-weight", "bold")
            .style("fill", "aliceblue")
            .text(title);

        const legend = svg.append('g')
            .attr('transform', `translate(0, ${radius + 40})`);

        legend.selectAll('rect')
            .data(data)
            .enter()
            .append('rect')
            .attr('x', -radius)
            .attr('y', (d, i) => i * 25)
            .attr('width', 15)
            .attr('height', 15)
            .style('fill', d => color(d.label));

        legend.selectAll('legend-labels')
            .data(data)
            .enter()
            .append('text')
            .attr('x', -radius + 20)
            .attr('y', (d, i) => i * 25 + 12)
            .style('font-size', '16px')
            .style('fill', 'aliceblue')
            .attr('alignment-baseline', 'middle')
            .each(function(d) {
                const el = d3.select(this);
                const label = `${d.label} (${d.value})`;

                const firstParenIndex = label.indexOf('(');

                if (firstParenIndex !== -1) {
                    const mainText = label.substring(0, firstParenIndex).trim();
                    const boldText = label.substring(firstParenIndex);

                    el.append('tspan')
                        .text(mainText + ' ')
                        .style('fill', 'aliceblue');

                    el.append('tspan')
                        .text(boldText)
                        .style('fill', labelColor)
                        .style('font-weight', 'bold');
                } else {
                    el.text(label)
                        .style('fill', 'aliceblue')
                        .style('font-weight', 'normal');
                }
            });

        svg.selectAll('labels')
            .data(data_ready)
            .enter()
            .append('text')
            .filter(d => {
                const percent = (d.data.value / total) * 100;
                return percent >= 5;
            })
            .text(d => {
                const percent = (d.data.value / total) * 100;
                return percent.toFixed(1) + '%';
            })
            .attr("transform", d => `translate(${arc.centroid(d)})`)
            .style("text-anchor", "middle")
            .style("font-size", "16px")
            .style("fill", labelColor)
            .style("font-weight", "bold");
    }

    if (typeof examStatistics !== 'undefined' && examStatistics != null) {
        const examData = [
            { label: 'Zaliczone Egzaminy', value: examStatistics.numberOfPassedExams },
            { label: 'Niezaliczone Egzaminy', value: examStatistics.numberOfSolvedExams - examStatistics.numberOfPassedExams }
        ];
        createPieChart(examData, 'exam-chart', 'Egzaminy', false);
    }

    if (typeof examStatistics !== 'undefined' && examStatistics != null) {
        const answerData = [
            { label: 'Poprawne Odpowiedzi', value: examStatistics.numberOfCorrectAnswers },
            { label: 'Pominięte Pytania', value: examStatistics.numberOfSkippedQuestions },
            { label: 'Błędne Odpowiedzi', value: examStatistics.numberOfIncorrectAnswers }
        ];
        createPieChart(answerData, 'answer-chart', 'Odpowiedzi', true);
    }

    if (typeof testStatsData !== 'undefined' && testStatsData != null) {
        const testAnswerData = [
            { label: 'Poprawne Odpowiedzi', value: testStatsData.numberOfQuestionsAnsweredCorrectly },
            { label: 'Pominięte Pytania', value: testStatsData.numberOfQuestionsSkipped },
            { label: 'Błędne Odpowiedzi', value: testStatsData.numberOfQuestionsAnsweredInCorrectly }
        ];
        createPieChart(testAnswerData, 'test-answer-chart', 'Odpowiedzi w Testach', true);
    }
});

