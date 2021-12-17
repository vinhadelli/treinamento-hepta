var app = document.getElementById("app");
var popup = document.getElementById("escolha-popup");
var lista = document.getElementById("lista");
var cadastro = document.getElementById("cadastro");

var listabtn = document.getElementById("listabtn");
var cadastrabtn = document.getElementById("cadastrabtn");

var op;
var editar;
var dropPopulado = false;
const url = "rs/concessionaria/";
var idEdit;
var fotosParaEnviar = [];

var clo = document.getElementsByClassName("close-popup")[0];

listabtn.onclick = function() {
    popup.style.display = "table";
    op = "listar";
}
cadastrabtn.onclick = function() {
    popup.style.display = "table";
    op = "cadastrar";
}

function voltar()
{
    if (lista.style.display == "table" || editar)
    {
        limparTabela();
    }
    if (document.getElementById("enviarbtn") != null)
    {   
        document.getElementById("enviarbtn").remove();
        document.getElementById("fotobreak").remove();        
    }
    if (document.getElementById("div-dinamica-foto") != null)
        document.getElementById("div-dinamica-foto").remove();

    lista.style.display = "none";    

    cadastro.style.display = "none";

    document.getElementById("formularioCarro")
        .style.display = "none";
    
    document.getElementById("formulario-montadora")
        .style.display = "none";
    
    document.getElementById("mostrar")
        .style.display = "none";

    app.style.display = "table";
}

function carroPopup()
{
    app.style.display = "none";
    popup.style.display = "none";

    switch (op)
    {
        case "listar":
            lista.style.display = "table";
            listarCarros();            
            break;
        case "cadastrar":
            cadastro.style.display = "table";
            MostrarCadastrarCarro();
            break;
    }
}

function montadoraPopup()
{
    app.style.display = "none";
    popup.style.display = "none";

    switch (op)
    {
        case "listar":
            lista.style.display = "table";
            listarMontadoras();
            break;
        case "cadastrar":
            cadastro.style.display = "table";
            MostrarCadastrarMontadora();
            break;
    }
}

async function receberFotos(id_carro)
{
    const resp = await fetch(url + "carro/foto/" + id_carro);
    
    if (resp.status != 200)
    {
        window.alert("Ocorreu um Erro: " + resp.status);
    }
    else
    {
        const list = await resp.json();

        let div = document.getElementById("divMostrarFotos");
        let dinamica = document.createElement('div');
        dinamica.id = "div-dinamica-foto";
        dinamica.style.display = "table";

        div.appendChild(dinamica);

        list.forEach(foto => popularFotos(foto));
    }
}

async function uploadFoto(id_carro, foto)
{
    const resp = await fetch(url+'carro/foto/'+id_carro,{
        method: 'POST',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(foto)
    });

    if (resp.status != 200)
        window.alert("Erro: " + resp.status);
    else
    {
        fotosParaEnviar = [];
        document.getElementById("foto-para-upload").remove();
        popularFotos(foto);
        }
}

async function pegarFoto(id_carro) {
    let opt =
    {
        types: [
            {
                description: 'Images',
                accept: {
                    'image/png': ['.png']
                }
            },
        ],
        excludeAcceptAllOption: true,
        multiple: false
    };
    let [input] = await window.showOpenFilePicker(opt);

    const file = await input.getFile();
    let array = await file.arrayBuffer();

    let foto_carregada = {
        id: id_carro,
        file: array
    }
    return foto_carregada;
}

async function carregarFoto(id_carro)
{
    let foto = await pegarFoto(id_carro);
    
    if (foto != null)
    {
        if (document.getElementById("enviarbtn") == null)
        {
            let enviarbtn = document.createElement('button');
            let br = document.createElement('br');
            br.id = "fotobreak"

            enviarbtn.onclick = function () {
                fotosParaEnviar.forEach(foto => uploadFoto(id_carro, foto))
                document.getElementById("enviarbtn").remove();
            };
            enviarbtn.innerText = "Enviar Fotos";
            enviarbtn.id = "enviarbtn";            

            document.getElementById("mostrar")
                .appendChild(enviarbtn);
            document.getElementById("mostrar")
                .appendChild(br);
        }

        let bytearray = new Uint8Array(foto.file);

        let showFoto = URL.createObjectURL(
            new Blob([bytearray.buffer], { type: 'image/png' }));

        let htmFoto = document.createElement('img');

        htmFoto.src = showFoto;
        htmFoto.id = "foto-para-upload";
        htmFoto.className = "thumb";

        foto.file = bufferArrayParaBase64(foto.file);
        
        fotosParaEnviar.push(foto);

        document.getElementById("mostrar").appendChild(htmFoto);
    }   
}

function MostrarCadastrarCarro()
{
    document.getElementById("titulo-cadastro")                     
        .textContent = 'Cadastrar Carro';
    
    document.getElementById("formularioCarro")
        .style.display = "table";
    document.getElementById("formulario-montadora")
        .style.display = "none";
    
    editar = false;

    popularDropMontadora();   
    limparCamposCadastro();
}

async function enviarCarro(form)
{
    let data = new FormData(form);

    if (!editar) {
        let carro = {
            modelo: data.get("fmodelo"),
            cor: data.get("fcor"),
            dataEntrada: data.get("fdata"),
            montadora: {
                id: data.get("fMontadora")
            }
        };

        const resp = await fetch(url + 'carro', {
            method: 'POST',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(carro)
        });

        const stat = resp.status;

        if (stat != 200)
            window.alert("Erro: "+stat);
    }
    else
    {
        let carro = {
            id: idEdit,
            modelo: data.get("fmodelo"),
            cor: data.get("fcor"),
            dataEntrada: data.get("fdata"),
            montadora: {
                id: data.get("fMontadora")
            }
        };

        const resp = await fetch(url + 'carro/'+idEdit, {
            method: 'PUT',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(carro)
        });

        const stat = resp.status;

        if (stat != 200)
            window.alert("Erro: "+stat);
              
    }
}

async function enviarMontadora(form)
{
    let data = new FormData(form);

    if (!editar) {
        let montadora = {
            nome: data.get("fnome"),
        };

        const resp = await fetch(url + 'montadora', {
            method: 'POST',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(montadora)
        });

        const stat = resp.status;

        if (stat != 200)
            window.alert("Erro: "+stat);
    }
    else
    {
        let montadora = {
            id: idEdit,
            nome: data.get("fnome")
        };

        const resp = await fetch(url + 'montadora/'+idEdit, {
            method: 'PUT',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(montadora)
        });

        const stat = resp.status;

        if (stat != 200)
            window.alert("Erro: "+stat);
              
    }
}

function MostrarCadastrarMontadora()
{
    document.getElementById("titulo-cadastro")
        .textContent = 'Cadastrar Montadora';
    
    document.getElementById("formulario-montadora")
        .style.display = "table"
    
    document.getElementById("formularioCarro")
        .style.display = "none";
    
    limparCamposCadastro();
}

function mostrarCarro(carro)
{
    document.getElementById("mostrar")
        .style.display = "table"
    
    lista.style.display = "none"; 

    receberFotos(carro.id);
    
    document.getElementById("titulo-carro")
        .textContent = carro.modelo;
    document.getElementById("pcor")
        .textContent = "Cor: "+carro.cor;
    document.getElementById("pdataentrada")
        .textContent = "Data de Entrada: " + formatarData(carro.dataEntrada);
    
    document.getElementById("cadastroFoto")
        .onclick = function () { carregarFoto(carro.id); };
    
    limparTabela();    
}

function mostrarEditarCarro(carro)
{
    document.getElementById("titulo-cadastro")                     
        .textContent = 'Editar Carro';
    
    document.getElementById("formularioCarro")
        .style.display = "table";
    
    cadastro.style.display = "table";
    lista.style.display = "none";
    
    popularDropMontadora();
    editar = true;
    
    document.querySelector("input[name=fmodelo]").value = carro.modelo;
	document.querySelector("input[name=fcor]").value = carro.cor;
	document.querySelector("input[name=fdata]").value = carro.dataEntrada;
    document.querySelector("select[name=fMontadora]").selectedIndex = (carro.montadora.id - 1);
    idEdit = carro.id;
}

function editarMontadora(montadora)
{
    document.getElementById("titulo-cadastro")                     
        .textContent = 'Editar Montadora';
    
    document.getElementById("formulario-montadora")
        .style.display = "table";
    
    cadastro.style.display = "table";
    lista.style.display = "none";

    editar = true;
    
    document.querySelector("input[name=fnome]").value = montadora.nome;

    idEdit = montadora.id;
}

async function deletarCarro(id)
{
    if (window.confirm("Deseja realmente apagar o Carro? Isso apagará todas as suas fotos"))
    {
        const resp = await fetch(url + 'carro/' + id, {
            method: 'DELETE'
        });

        if (resp.status != 200)
            window.alert("Erro: "+stat);

        else
        {
            limparTabela();
            listarCarros();
        }
    }
}

async function deletarMontadora(id)
{
    if (window.confirm("Deseja realmente apagar a Montadora? Isso apagará todos os Carros atribuidos a ela"))
    {
        const resp = await fetch(url + 'montadora/' + id, {
            method: 'DELETE'
        });

        if (resp.status != 200)
            window.alert("Erro: " + resp.status);

        limparTabela();
        listarMontadoras();
    }
}

async function deletarFoto(id, foto)
{
    if (window.confirm("Deseja realmente apagar a Foto?"))
    {
        foto.file = '';
        const resp = await fetch(url + 'carro/foto/' + id, {
            method: 'DELETE',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(foto)
        });

        if (resp.status != 200)
            window.alert("Erro: " + stat);
        else
        {
            document.getElementById("div-dinamica-foto").remove();
            receberFotos(foto.id_Carro);
        }
    }
}

async function listarCarros()
{
    document.getElementById("titulo-lista")
        .textContent = 'Listar Carros';

    let carrourl = url + "carro";
    let response = await fetch(carrourl);
    if(response.ok)
    {
        let json = await response.json();

        popularTabela("tabela-dinamica", json, "carro");
        popularDropMontadora();
    }
    else
    {
        window.alert("Erro ao resgatar os Carros: "+response.status);
    }
}

async function listarMontadoras()
{
    document.getElementById("titulo-lista")
        .textContent = 'Listar Montadoras';
    
    let montadoraurl = url + "montadora";
    let response = await fetch(montadoraurl);
    if(response.ok)
    {
        let json = await response.json();

        popularTabela("tabela-dinamica", json, "montadora");
    }
    else
    {
        window.alert("Erro ao resgatar as montadoras: "+response.status);
    }
}

function reordenarTabela(table, data, id)
{
    limparTabela();
    popularTabela(table, data, id);
}

function popularTabela(table, data, id)
{
    let tab = document.getElementById(table);
    let tbody = document.createElement("tbody");

    if (id == "carro") {
        
        let ro = document.createElement('tr');
        ro.appendChild(document.createElement('th'));
        ro.cells[0].appendChild(document.createTextNode("Montadora"));
        ro.cells[0].onclick = function () {
            data.sort((x, y) => {
                let n1 = x.montadora.nome.toUpperCase();
                let n2 = y.montadora.nome.toUpperCase();
                if (n1 < n2) {
                    return -1;
                }
                if (n1 > n2) {
                    return 1;
                }
                return 0;
            });
            reordenarTabela(table, data, id);
        };
        ro.appendChild(document.createElement('th'));
        ro.cells[1].appendChild(document.createTextNode("Modelo"));
        ro.cells[1].onclick = function () {
            data.sort((x, y) => {
                let n1 = x.modelo.toUpperCase();
                let n2 = y.modelo.toUpperCase();
                if (n1 < n2) {
                    return -1;
                }
                if (n1 > n2) {
                    return 1;
                }
                return 0;
            });
            reordenarTabela(table, data, id);
        };
        ro.appendChild(document.createElement('th'));
        ro.cells[2].appendChild(document.createTextNode("Cor"));
        ro.cells[2].onclick = function () {
            data.sort((x, y) => {
                let n1 = x.cor.toUpperCase();
                let n2 = y.cor.toUpperCase();
                if (n1 < n2) {
                    return -1;
                }
                if (n1 > n2) {
                    return 1;
                }
                return 0;
            });
            reordenarTabela(table, data, id);
        };
        ro.appendChild(document.createElement('th'));
        ro.cells[3].appendChild(document.createTextNode("Data de Entrada"));
        ro.cells[3].onclick = function () {
            data.sort((x, y) => {
                return new Date(x.dataEntrada) - new Date(y.dataEntrada);
            });
            reordenarTabela(table, data, id);
        };

        tbody.appendChild(ro);

        data.forEach(element => {
            let tr = document.createElement('tr');

            tr.appendChild(document.createElement('td'));
            tr.cells[0].appendChild(document.createTextNode(element.montadora.nome));

            tr.appendChild(document.createElement('td'));
            tr.cells[1].appendChild(document.createTextNode(element.modelo));

            tr.appendChild(document.createElement('td'));
            tr.cells[2].appendChild(document.createTextNode(element.cor));

            tr.appendChild(document.createElement('td'));
            tr.cells[3].appendChild(document.createTextNode(formatarData(element.dataEntrada)));

            tr.appendChild(document.createElement('td'));
            let span1 = document.createElement('span');
            span1.appendChild(document.createTextNode('👁'))
            span1.onclick = function() {mostrarCarro(element);};
            tr.cells[4].appendChild(span1);

            tr.appendChild(document.createElement('td'));
            let span2 = document.createElement('span');
            span2.appendChild(document.createTextNode('✎'))
            span2.onclick = function() {mostrarEditarCarro(element);};
            tr.cells[5].appendChild(span2);

            tr.appendChild(document.createElement('td'));
            let span3 = document.createElement('span');
            span3.appendChild(document.createTextNode('✖'));
            span3.onclick = function() {deletarCarro(element.id);};
            tr.cells[6].appendChild(span3);
            
            tbody.appendChild(tr);
        });
        tab.appendChild(tbody);

    }
    else
    {
        let ro = document.createElement('tr');
        ro.appendChild(document.createElement('th'));
        ro.cells[0].appendChild(document.createTextNode("Nome"));
        
        tbody.appendChild(ro);

        data.forEach(element => {
            let tr = document.createElement('tr');

            tr.appendChild(document.createElement('td'));
            tr.cells[0].appendChild(document.createTextNode(element.nome));

            tr.appendChild(document.createElement('td'));
            let span1 = document.createElement('span');
            span1.appendChild(document.createTextNode('✎'))
            span1.onclick = function() {editarMontadora(element);};
            tr.cells[1].appendChild(span1);

            tr.appendChild(document.createElement('td'));
            let span2 = document.createElement('span');
            span2.appendChild(document.createTextNode('✖'));
            span2.onclick = function() {deletarMontadora(element.id);};
            tr.cells[2].appendChild(span2);
            
            tbody.appendChild(tr);
        });
        tab.appendChild(tbody);
    }
}

async function popularDropMontadora()
{
    if (!dropPopulado)
    {
        let dropMont = document.getElementById("drop-montadoras");
        let lista;

        let montadoraurl = url + "montadora";
        let response = await fetch(montadoraurl);
        if(response.ok)
        {
            lista = await response.json();
        }
        else
        {
            window.alert("Erro ao resgatar as montadoras: "+response.status);
        }

    
        lista.forEach(mont => {
        let op = document.createElement('option');
        op.value = mont.id;
        op.innerText = mont.nome;
        dropMont.appendChild(op);
        });   
    }
    dropPopulado = true;
}

function popularFotos(foto)
{
    let div = document.getElementById("div-dinamica-foto");
    let cont = document.createElement('div');

    cont.className = "cont-image";

    let del = document.createElement('i');
    del.onclick = function () { deletarFoto(foto.id, foto); };
    del.innerText = "✖";
    

    let img = document.createElement('img');
    img.src = "data:image/png;base64, " + foto.file;
    img.className = "thumb";
    img.onclick = function () {
            var w = window.open("");
        w.document.write(img.outerHTML);
    }

    cont.appendChild(img);
    cont.appendChild(del);

    div.appendChild(cont);
}

function formatarData(date)
{
    return date.slice(8, 10) + '/' + date.slice(5, 7) + '/' + date.slice(0, 4);
}

function limparFotos()
{
    documento.getElementById("div-dinamica-foto").remove();
}

function limparTabela()
{
    let tab = document.getElementById("tabela-dinamica");
    tab.removeChild(document.getElementsByTagName("tbody")[0]);
}

function limparCamposCadastro()
{
    document.querySelector("input[name=fmodelo]").value = '';
	document.querySelector("input[name=fcor]").value = '';
	document.querySelector("input[name=fdata]").value = '';
    document.querySelector("select[name=fMontadora]").selectedIndex = 0;

    document.querySelector("input[name=fnome]").value = '';
}

function bufferArrayParaBase64( buffer ) {
    var binary = '';
    var bytes = new Uint8Array( buffer );
    var len = bytes.byteLength;
    for (var i = 0; i < len; i++) {
        binary += String.fromCharCode( bytes[ i ] );
    }
    return window.btoa( binary );
}

clo.onclick = function() {
  popup.style.display = "none";
}

window.onclick = function(event) {
  if (event.target == popup) {
    popup.style.display = "none";
  }
}
