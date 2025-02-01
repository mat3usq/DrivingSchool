function initializeDragAndDrop(sublectureIndex, subjectIndex) {
	const inputFileDiv = document.querySelector(
		`div.input-file[data-sublecture="${sublectureIndex}"][data-subject="${subjectIndex}"]`
	)
	const dragArea = inputFileDiv.querySelector('.drag-area')
	const dragText = dragArea.querySelector('.header.first')
	const secondHeader = dragArea.querySelector('.header.second')
	const titleFile = inputFileDiv.querySelector('.title-file')
	const button = inputFileDiv.querySelector('span.button')
	let input = inputFileDiv.querySelector(
		`input[type="file"][name="sublectures[${sublectureIndex}].subjects[${subjectIndex}].file"]`
	)
	let file

	if (!input && window.location.href.includes('lecture/editSubject')) {
		input = document.getElementById(`sublectures[${sublectureIndex}].subjects[${subjectIndex}]`);
	}

	button.onclick = () => {
		input.click()
	}

	input.addEventListener('change', function () {
		file = this.files[0]
		dragArea.classList.add('active')
		displayFile()
	})

	dragArea.addEventListener('dragover', event => {
		event.preventDefault()
		if (document.elementFromPoint(event.clientX, event.clientY) === dragArea) {
			dragText.textContent = 'Opuść przesyłany Plik'
			dragArea.classList.add('active')
			secondHeader.style.display = 'none'
		}
	})

	dragArea.addEventListener('dragleave', event => {
		const rect = dragArea.getBoundingClientRect()
		if (
			event.clientX < rect.left ||
			event.clientX > rect.right ||
			event.clientY < rect.top ||
			event.clientY > rect.bottom
		) {
			dragText.textContent = 'Przeciągnij & Upuść'
			dragArea.classList.remove('active')
			secondHeader.style.display = 'block'
		}
	})

	dragArea.addEventListener('drop', event => {
		event.preventDefault();
		const dt = new DataTransfer();
		dt.items.add(event.dataTransfer.files[0]);
		input.files = dt.files;

		file = input.files[0];
		displayFile();
	});

	function displayFile() {
		if (file) {
			let validExntensions = ['image/jpeg', 'image/jpg', 'image/png']
			if (validExntensions.includes(file.type)) {
				let fileReader = new FileReader()
				fileReader.onload = () => {
					let fileUrl = fileReader.result
					dragArea.innerHTML = `<img src="${fileUrl}" alt="">`
				}
				fileReader.readAsDataURL(file)

				let fileName = file.name
				let fileSize = file.size
				if (fileName.length > 20) {
					fileName = fileName.substring(0, 20) + '...'
				}
				let fileSizeFormatted = (fileSize / 1024).toFixed(2) + ' KB'
				if (fileSize > 1024 * 1024) {
					fileSizeFormatted = (fileSize / (1024 * 1024)).toFixed(2) + ' MB'
				}

				titleFile.innerHTML = `Przesłano: ${fileName}, - ${fileSizeFormatted}`
			} else {
				titleFile.innerHTML = 'Nieprawidłowy plik'
				dragArea.classList.remove('active')
			}
		}
	}
}
