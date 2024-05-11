let sublectureCounters = 0
let subjectCounters = []

function addSublectureForm() {
	if (sublectureCounters < 10) {
		const container = document.getElementById('sublecturesContainer')
		subjectCounters[sublectureCounters] = 0

		const htmlContent = `
<div id="sublecture-${sublectureCounters}">
<p class="formTitle">Dodawanie ${sublectureCounters + 1}. Podwykładu</p>
<div class="input-field">
    <i class="fas fa-book"></i>
    <input type="text" name="sublectures[${sublectureCounters}].title" placeholder="Nazwa Podwykładu">
</div>
<div class="input-field">
    <i class="fas fa-align-left"></i>
    <textarea name="sublectures[${sublectureCounters}].content" placeholder="Treść Podwykładu" rows="4"></textarea>
</div>
<div class="box-btn" id="container-${sublectureCounters}">
    <button type="button" class="delete-btn" onclick="removeSublectureForm(${sublectureCounters})">Usuń Podwykład</button>
    <button type="button" class="info-btn" onclick="addSubjectForm(${sublectureCounters})">Dodaj Rozdział</button>
</div>
</div>
`
		container.insertAdjacentHTML('beforeend', htmlContent)
		sublectureCounters++
	}
}

function removeSublectureForm(sublectureIndex) {
	let sublectureForm = document.getElementById(`sublecture-${sublectureIndex}`)
	sublectureForm.remove()
	sublectureCounters--
	subjectCounters[sublectureIndex] = 0

	updateFollowingSublectures(sublectureIndex)
}

function updateFollowingSublectures(sublectureIndex) {
	let start = sublectureIndex + 1

	while (document.getElementById(`sublecture-${start}`)) {
		const current = start
		const next = current - 1

		let currentSublecture = document.getElementById(`sublecture-${current}`)
		if (!currentSublecture) break

		currentSublecture.id = `sublecture-${next}`

		const titleElement = currentSublecture.querySelector('.formTitle')
		if (titleElement) {
			titleElement.textContent = `Dodawanie ${next + 1}. Podwykładu`
		}

		const inputs = currentSublecture.querySelectorAll('input, textarea')
		inputs.forEach(input => {
			const nameAttr = input.getAttribute('name')
			input.setAttribute('name', nameAttr.replace(`[${current}]`, `[${next}]`))
		})

		const btnContainer = document.getElementById(`container-${current}`)
		btnContainer.id = `container-${next}`
		const btns = btnContainer.querySelectorAll('button')
		btns.forEach(btn => {
			const onClickAttr = btn.getAttribute('onclick')
			btn.setAttribute('onclick', onClickAttr.replace(`(${current})`, `(${next})`))
		})

		for (let i = 0; i < subjectCounters[current]; i++) {
			let currentSubject = document.getElementById(`subject-${current}-${i}`)
			if (currentSubject) {
				currentSubject.id = `subject-${next}-${i}`

				const subjectInputs = currentSubject.querySelectorAll('input, textarea')
				subjectInputs.forEach(input => {
					const nameAttr = input.getAttribute('name')
					input.setAttribute('name', nameAttr.replace(`sublectures[${current}]`, `sublectures[${next}]`))
				})

				const deleteButton = currentSubject.querySelector('.delete-btn')
				if (deleteButton) {
					deleteButton.setAttribute('onclick', `removeSubjectForm(${next}, ${i})`)
				}

				const fileInputDiv = currentSubject.querySelector('.input-file')
				if (fileInputDiv) {
					fileInputDiv.setAttribute('data-sublecture', next)
					fileInputDiv.setAttribute('data-subject', i)
				}
			}
		}

		subjectCounters[next] = subjectCounters[current]
		subjectCounters[current] = 0

		start++
	}
}

function addSubjectForm(sublectureIndex) {
	if (subjectCounters[sublectureIndex] < 10) {
		const subjectIndex = subjectCounters[sublectureIndex]
		const btnContainer = document.getElementById(`container-${sublectureIndex}`)
		const htmlContent = `
<div id="subject-${sublectureIndex}-${subjectIndex}">
    <p class="formTitle">Dodawanie ${subjectIndex + 1}. Rozdziału</p>
    <div class="input-field">
        <i class="fas fa-bookmark"></i>
        <input type="text" name="sublectures[${sublectureIndex}].subjects[${subjectIndex}].title" placeholder="Nazwa Rozdziału">
    </div>
    <div class="input-file" data-sublecture="${sublectureIndex}" data-subject="${subjectIndex}">
        <h3 class="title-file">Prześlij swój plik</h3>
            <div class="drag-area">
                <div class="icon">
                    <i class="fas fa-images"></i>
                </div>
                <span class="header first">Przeciągnij & Upuść</span>
                <span class="header second">lub <span class="button">Zaimportuj</span></span>
                <span class="support">Wspieramy: JPEG, PNG, JPG</span>
            </div>
            <input type="file" hidden name="sublectures[${sublectureIndex}].subjects[${subjectIndex}].file" accept="image/*">
    </div>
    <div class="input-field">
        <i class="fas fa-align-left"></i>
        <textarea name="sublectures[${sublectureIndex}].subjects[${subjectIndex}].content" placeholder="Treść Rozdziału" rows="4"></textarea>
    </div>

    <div class="box-btn">
        <button type="button" class="delete-btn" onclick="removeSubjectForm(${sublectureIndex}, ${subjectIndex})">Usuń Rozdział</button>
    </div>
</div>
`
		btnContainer.insertAdjacentHTML('beforebegin', htmlContent)
		subjectCounters[sublectureIndex]++
		initializeDragAndDrop(sublectureIndex, subjectIndex)
	}
}

function removeSubjectForm(sublectureIndex, subjectIndex) {
	let subjectForm = document.getElementById(`subject-${sublectureIndex}-${subjectIndex}`)
	subjectForm.remove()
	subjectCounters[sublectureIndex]--
	updateFollowingSubjects(sublectureIndex, subjectIndex)
}

function updateFollowingSubjects(sublectureIndex, subjectIndex) {
	let start = subjectIndex + 1
	const sublecturePrefix = `subject-${sublectureIndex}-`

	while (document.getElementById(`${sublecturePrefix}${start}`)) {
		const current = start
		const next = current - 1
		const currentSubject = document.getElementById(`${sublecturePrefix}${current}`)

		if (!currentSubject) break

		currentSubject.id = `${sublecturePrefix}${next}`

		const titleElement = currentSubject.querySelector('.formTitle')
		if (titleElement) {
			titleElement.textContent = `Dodawanie ${next + 1}. Rozdziału`
		}

		const inputs = currentSubject.querySelectorAll('input, textarea')
		inputs.forEach(input => {
			const nameAttr = input.getAttribute('name')
			if (nameAttr) {
				input.setAttribute('name', nameAttr.replace(`[${current}]`, `[${next}]`))
			}
		})

		const fileInputDiv = currentSubject.querySelector('.input-file')
		if (fileInputDiv) {
			fileInputDiv.setAttribute('data-sublecture', sublectureIndex)
			fileInputDiv.setAttribute('data-subject', next)
		}

		const deleteButton = currentSubject.querySelector('.delete-btn')
		if (deleteButton) {
			deleteButton.setAttribute('onclick', `removeSubjectForm(${sublectureIndex}, ${next})`)
		}

		start++
	}
}
