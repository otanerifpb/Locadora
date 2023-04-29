package br.edu.ifpb.pweb2.academiConect.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.edu.ifpb.pweb2.academiConect.model.Estudante;
import br.edu.ifpb.pweb2.academiConect.model.Instituicao;
import br.edu.ifpb.pweb2.academiConect.repository.EstudanteRepository;
import br.edu.ifpb.pweb2.academiConect.repository.InstituicaoRepository;
import br.edu.ifpb.pweb2.academiConect.repository.PeriodoRepository;

// Rota para acessar a class
@Controller
@RequestMapping("/instituicoes")
public class InstituicaoController {
    @Autowired
    EstudanteRepository estudanteRepository;
    
    @Autowired
    InstituicaoRepository instituicaoRepository;

    @Autowired
    PeriodoRepository periodoRepository;

    // Rota para acessar a lista pelo menu com o GET
    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView listAll(ModelAndView model) {
        model.addObject("instituicoes", instituicaoRepository.findAll());
        model.setViewName("instituicoes/listInst");
        return model;
    }

    // Rota para acessar a lista ao usar a Rota da class
    @RequestMapping()
    public String listAll(Model model) {
        model.addAttribute("instituicoes", instituicaoRepository.findAll());
        return "instituicoes/listInst";
    }
    
    // Rota para acessar o formunário
    @RequestMapping("/formInst")
    public ModelAndView getForm(ModelAndView model) {
        model.addObject("instituicao", new Instituicao());
        model.setViewName("instituicoes/formInst");
        return model;
    }

    // Rota para acessar o formunlário de atualização ou a lista se não for atualizar 
    @RequestMapping("/{id}")
    public ModelAndView getInstituicaoById(@PathVariable(value = "id") Integer id, ModelAndView model) {
        Optional<Instituicao> opInstituicao = instituicaoRepository.findById(id);
        Instituicao instituicao = opInstituicao.get();
        if (opInstituicao.isPresent()) {
            model.addObject("instituicao", opInstituicao.get());
            model.setViewName("instituicoes/formUpInst");
        } else {
            model.addObject("errorMensagem", "Instituição "+instituicao.getNome()+" não encontrado.");
            model.setViewName("instituicoes/listInst");
        }
        return model;
    }

    // Rota para salvar novo objeto na lista
    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView save(Instituicao instituicao, ModelAndView model, RedirectAttributes redAttrs) {
        Optional<Instituicao> opInstituicao = instituicaoRepository.findBySigla(instituicao.getSigla());
        if (opInstituicao.isPresent()) {
            redAttrs.addFlashAttribute("errorMensagem", "Instituição "+instituicao.getSigla()+" já cadastrado no sistema!!");
            //model.addObject("errorMensagem", "Instituição "+instituicao.getSigla()+" já existe!!");
            model.setViewName("redirect:/instituicoes");
        } else {
            instituicaoRepository.save(instituicao);
            model.addObject("instituicoes", instituicaoRepository.findAll());
            redAttrs.addFlashAttribute("succesMensagem", "Instituição cadastrada com sucesso!");
            //model.addObject("successMensagem", "Instituição cadastrado com sucesso!");
            model.setViewName("redirect:/instituicoes");
        }  
        return model;
    }

    // Rota para atualizar um objeto na lista
    @RequestMapping(value="/update", method = RequestMethod.POST)
    public ModelAndView updade(Instituicao instituicao, ModelAndView model) {
        //Optional<Instituicao> listaInstituicao = instituicaoRepository.findBySigla(instituicao.getSigla());
        instituicaoRepository.save(instituicao);
        model.addObject("instituicoes", instituicaoRepository.findAll());
        //redAttrs.addFlashAttribute("succesMensagem", "Instituição atualizada com sucesso!");
        model.addObject("succesMensagem", "Instituição "+instituicao.getNome()+", atualizada com sucesso!");
        //model.setViewName("redirect:instituicoes");
        model.setViewName("redirect:/instituicoes");
        return model;
    }

    // Rota para deletar um objeto da lista
    @RequestMapping("{id}/delete")
    public ModelAndView deleteById(@PathVariable(value = "id") Integer id, ModelAndView model, RedirectAttributes redAtt) {
        Optional<Instituicao> opInstituicao = instituicaoRepository.findById(id);
        Instituicao instituicao = null;
        if (opInstituicao.isPresent()) {
            instituicao = opInstituicao.get();
            List<Estudante> listEstudantes = instituicao.getEstudantes();
            if(listEstudantes.isEmpty()){
                instituicaoRepository.deleteById(id);
            }
            //instituicao.setPeriodoLetivo(null);
            redAtt.addFlashAttribute("succesMensagem", "Instituicao "+instituicao.getNome()+" deletado com sucesso!!");
        } else {
            redAtt.addFlashAttribute("errorMensagem", "Instituição não pode ser deletada, contem estudante cadastrado.");
        }
        model.setViewName("redirect:/instituicoes");
        return model;
    }

    // Rota para a dependência da Class A com a Class B
    // @ModelAttribute("estudanteItems")
    // public List<Estudante> getEstudantes() {
    //     return estudanteRepository.findAll();
    // }


    

    // @RequestMapping("/matricula")
    // public String getPeriodo() {
    //     return "intituicoes/periodoAtual";
    // }

    // @RequestMapping(value = "/periodoAtual")
    // public String periodoAtualInstituicao(String matricula, Declaracao declaracao, Model model) {
    //     String proxPagina = "";
    //     if (matricula != null && declaracao.getDataRecebimento() == null) {
    //         Instituicao instituicao = instituicaoRepository.findByMatriculaWithDeclaracoes(matricula);
    //         if (instituicao != null) {
    //             model.addAttribute("instituicao", instituicao);
    //             model.addAttribute("declaracao", declaracao);
    //             proxPagina = "instituicoes/periodoAtual";
    //         } else {
    //             model.addAttribute("mensagem", "Instituição inexistente!");
    //             proxPagina = "instituicoes/periodoAtual";
    //         }
    //     } else {
    //         Instituicao instituicao = instituicaoRepository.findById(null);
    //        // instituicao.addDeclaracao(declaracao);
    //         instituicaoRepository.save(instituicao);
    //         proxPagina = addDeclaracaoInstituicao(instituicao.getId(), model);
    //     }
    //     return proxPagina;
    // }

    // @RequestMapping(value = "/{id}/declaracoes")
    // public String addDeclaracaoInstituicao(@PathVariable("id") Integer idInstituicao, Model model) {
    //    // Instituicao instituicao = instituicaoRepository.findByIdWithDeclaracoes(idInstituicao);
    //    Optional<Instituicao> instituicao = instituicaoRepository.findById(idInstituicao);
    //    Instituicao intitui = instituicao.get();
    //     model.addAttribute("instituicao", intitui);
    //     return "instituicoes/declaracoes";
    // }

     // Ativa o menu Instituição na barra de navegação
     @ModelAttribute("menu")
     public String activeMenu(){
         return "instituicoes";
     }
    
}
