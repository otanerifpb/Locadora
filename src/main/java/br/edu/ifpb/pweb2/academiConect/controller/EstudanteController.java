package br.edu.ifpb.pweb2.academiConect.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.edu.ifpb.pweb2.academiConect.model.Declaracao;
import br.edu.ifpb.pweb2.academiConect.model.Estudante;
import br.edu.ifpb.pweb2.academiConect.repository.DeclaracaoRepository;
import br.edu.ifpb.pweb2.academiConect.repository.EstudanteRepository;
import br.edu.ifpb.pweb2.academiConect.repository.InstituicaoRepository;

import java.util.Optional;
import java.util.Set;

@Controller
// Rota para o acesso da class
@RequestMapping("/estudantes")
public class EstudanteController {
    @Autowired
    EstudanteRepository estudanteRepository;
    
    @Autowired
    InstituicaoRepository instituicaoRepository; 

    @Autowired
    DeclaracaoRepository declaracaoRepository; 

    // Rota para acessar a lista pelo menu
    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView listAll(ModelAndView model) {
        model.addObject("estudantes", estudanteRepository.findAll());
        model.setViewName("estudantes/listEstu");
        return model;
    }

    // Rota para acessar a lista ao usar o REDIRECT
    @RequestMapping()
    public String listAll(Model model) {
        model.addAttribute("estudantes", estudanteRepository.findAll());
        return "estudantes/listEstu";
    }

    // Rota para acessar o formunário
    @RequestMapping("/formEstu")
    public ModelAndView getFormEstu(Estudante estudante, ModelAndView model) {
        model.addObject("estudante", estudante);
        model.setViewName("estudantes/formEstu");
        return model;
    }

    // Rota para acessar o formunlário de atualização ou a lista se não for atualizar 
    @RequestMapping("/{id}")
    public ModelAndView getEstudanteById(@PathVariable(value = "id") Integer id, ModelAndView model) {
        Optional<Estudante> opEstudante = estudanteRepository.findById(id);
        Estudante estudante = opEstudante.get();
        if (opEstudante.isPresent()) {
            model.addObject("estudante", opEstudante.get());
            model.setViewName("estudantes/formUpEstu");
        } else {
            model.addObject("errorMensagem", "Estudante "+estudante.getNome()+" não encontrado.");
            model.setViewName("estudantes/listInst");
        }
        return model;
    }

    // Rota para salvar novo objeto na lista
    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView save(Estudante estudante, ModelAndView model, RedirectAttributes redAttrs) {
        Optional<Estudante> opEmail = estudanteRepository.findByEmail(estudante.getEmail());
        Optional<Estudante> opMatricula = estudanteRepository.findByMatricula(estudante.getMatricula());
        if (opMatricula.isPresent()) {
            redAttrs.addFlashAttribute("errorMensagem", "Matrícula já cadastrada no sistema!!");
            model.setViewName("redirect:estudantes");
        } else if (opEmail.isPresent()) {
            redAttrs.addFlashAttribute("errorMensagem", "E-mail já cadastrado no sistema!!");
            model.setViewName("redirect:estudantes");
        } else {
            estudanteRepository.save(estudante);
            model.addObject("estudantes", estudanteRepository.findAll());
            redAttrs.addFlashAttribute("succesMensagem", "Estudante cadastrado com sucesso!");
            //model.setViewName("redirect:estudantes");
            model.setViewName("estudantes/listEstu");
        }
        return model;
    }

    // Rota para atualizar um objeto na lista
    @RequestMapping(value="/update", method = RequestMethod.POST)
    public ModelAndView updade(Estudante estudante, ModelAndView model) {
        //Optional<Instituicao> listaInstituicao = instituicaoRepository.findBySigla(instituicao.getSigla());
        estudanteRepository.save(estudante);
        model.addObject("estudantes", estudanteRepository.findAll());
        //redAttrs.addFlashAttribute("succesMensagem", "Instituição atualizada com sucesso!");
        model.addObject("succesMensagem", "Estudante "+estudante.getNome()+", atualizado com sucesso!");
        //model.setViewName("redirect:instituicoes");
        model.setViewName("estudantes/listEstu");
        return model;
    }

    // Rota para deletar um objeto da lista
    @RequestMapping("{id}/delete")
    public ModelAndView deleteById(@PathVariable(value = "id") Integer id, ModelAndView model, RedirectAttributes redAtt) {
        Optional<Estudante> opEstudante = estudanteRepository.findById(id);
        if (opEstudante.isPresent()) {
            Estudante estudante = opEstudante.get();
            Set<Declaracao> listaDeclaracao = estudante.getDeclaracoes();
            declaracaoRepository.deleteAll(listaDeclaracao);
            estudante.setInstituicao(null);
            estudanteRepository.deleteById(id);
            redAtt.addFlashAttribute("succesMensagem", "Estudante "+estudante.getNome()+" deletado com sucesso!!");
        } else {
            redAtt.addFlashAttribute("errorMensagem", "Estudante Não encontrado!!");
        }
        model.setViewName("estudantes/listEstu");
        return model;
    }

    // Ativa o menu Estudante na barra de navegação
    @ModelAttribute("menu")
    public String activeMenu(){
        return "estudantes";
    }
    
}
