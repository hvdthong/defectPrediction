package org.apache.synapse.config.xml;

import org.apache.axiom.om.OMElement;
import org.apache.synapse.Mediator;
import org.apache.synapse.mediators.builtin.CacheMediator;

/**
 * Serializes the Cache mediator to the XML configuration specified
 *
 * &lt;cache (id="string")? hashGenerator="class" scope="string" timeout="mili-seconds"&gt;
 *  &lt;onCacheHit (sequence="key")?&gt;
 *   (mediator)+
 *  &lt;/onCacheHit&gt;
 *  &lt;implementation type=(memory | disk) maxSize="int"/&gt;
 * &lt;/cache&gt;
 */
public class CacheMediatorSerializer extends AbstractMediatorSerializer {

    public OMElement serializeMediator(OMElement parent, Mediator m) {

        if (!(m instanceof CacheMediator)) {
            handleException("Unsupported mediator passed in for serialization : " + m.getType());
        }
        CacheMediator mediator = (CacheMediator) m;
        OMElement cache = fac.createOMElement("cache", synNS);
        saveTracingState(cache, mediator);

        if (mediator.getId() != null) {
            cache.addAttribute(fac.createOMAttribute("id", nullNS, mediator.getId()));
        }

        if (mediator.getDigestGenerator() != null) {
            cache.addAttribute(fac.createOMAttribute("hashGenerator", nullNS,
                mediator.getDigestGenerator().getClass().getName()));
        }

        if (mediator.getScope() != null) {
            cache.addAttribute(fac.createOMAttribute("scope", nullNS, mediator.getScope()));
        }

        if (mediator.getTimeout() != 0) {
            cache.addAttribute(
                fac.createOMAttribute("timeout", nullNS, Long.toString(mediator.getTimeout())));
        }

        if (mediator.getOnCacheHitRef() != null) {
            OMElement onCacheHit = fac.createOMElement("onCacheHit", synNS);
            onCacheHit.addAttribute(
                fac.createOMAttribute("sequence", nullNS, mediator.getOnCacheHitRef()));
            cache.addChild(onCacheHit);
        } else if (mediator.getOnCacheHitSequence() != null) {
            OMElement onCacheHit = fac.createOMElement("onCacheHit", synNS);
            new SequenceMediatorSerializer().serializeChildren(
                onCacheHit, mediator.getOnCacheHitSequence().getList());
            cache.addChild(onCacheHit);
        }

        if (mediator.getInMemoryCacheSize() != 0) {
            OMElement implElem = fac.createOMElement("implementation", synNS);
            implElem.addAttribute(fac.createOMAttribute("type", nullNS, "memory"));
            implElem.addAttribute(fac.createOMAttribute(
                "maxSize", nullNS, Integer.toString(mediator.getInMemoryCacheSize())));
            cache.addChild(implElem);
        }
        
        if (mediator.getDiskCacheSize() != 0) {
            OMElement implElem = fac.createOMElement("implementation", synNS);
            implElem.addAttribute(fac.createOMAttribute("type", nullNS, "disk"));
            implElem.addAttribute(fac.createOMAttribute(
                "maxSize", nullNS, Integer.toString(mediator.getDiskCacheSize())));
            cache.addChild(implElem);
        }

        if (parent != null) {
            parent.addChild(cache);
        }

        return cache;
    }

    public String getMediatorClassName() {
        return CacheMediator.class.getName();
    }
}
